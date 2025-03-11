package com.tripmarket.domain.auth.service

import com.tripmarket.domain.auth.dto.LoginRequestDto
import com.tripmarket.domain.auth.dto.SignupRequestDto
import com.tripmarket.domain.member.entity.Member
import com.tripmarket.domain.member.repository.MemberRepository
import com.tripmarket.global.exception.CustomException
import com.tripmarket.global.exception.ErrorCode
import com.tripmarket.global.jwt.JwtTokenProvider
import com.tripmarket.global.security.CustomUserDetails
import com.tripmarket.global.util.CookieUtil
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class AuthService(
    private val authenticationManager: AuthenticationManager,
    private val jwtTokenProvider: JwtTokenProvider,
    private val redisTemplate: RedisTemplate<String, String>,
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder,
    private val cookieUtil: CookieUtil
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    fun logout(request: HttpServletRequest, response: HttpServletResponse) {
        log.debug("로그아웃 요청 수신")

        val logoutRefreshToken = cookieUtil.extractRefreshTokenFromCookie(request)
        val logoutAccessToken = cookieUtil.extractAccessTokenFromCookie(request)

        // 1. RefreshToken에서 사용자 ID 추출
        val userId = jwtTokenProvider.getUserIdFromRefreshToken(logoutRefreshToken)

        // 2. AccessToken이 유효하면 블랙리스트 추가
        if (jwtTokenProvider.validateToken(logoutAccessToken)) {
            jwtTokenProvider.addToBlacklist(logoutAccessToken)
            log.info("AccessToken 블랙리스트에 추가 완료 - userId: {}", userId)
        }

        // 3. Redis에서 리프레시 토큰 삭제
        deleteRefreshToken(userId)

        // 4. 쿠키 삭제 및 빈 쿠키 반환
        val emptyAccessCookie = cookieUtil.createLogoutAccessCookie()
        val emptyRefreshCookie = cookieUtil.createLogoutRefreshCookie()

        response.addHeader(HttpHeaders.SET_COOKIE, emptyAccessCookie.toString())
        response.addHeader(HttpHeaders.SET_COOKIE, emptyRefreshCookie.toString())
    }

    @Transactional
    fun signUp(signUpRequestDto: SignupRequestDto) {
        // 중복 검사
        if (memberRepository.findByEmail(signUpRequestDto.email).isPresent) {
            throw CustomException(ErrorCode.DUPLICATE_EMAIL)
        }

        val member = Member.createNormalMember(signUpRequestDto, passwordEncoder)
        memberRepository.save(member)
    }

    @Transactional
    fun login(loginRequestDto: LoginRequestDto): Map<String, String> {
        log.info("로그인 요청 - email: {}", loginRequestDto.email)

        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(loginRequestDto.email, loginRequestDto.password)
        )

        val userDetails = authentication.principal as CustomUserDetails
        val member = userDetails.member

        // JWT 토큰 생성
        val accessToken = jwtTokenProvider.createAccessToken(authentication)
        val refreshToken = jwtTokenProvider.createRefreshToken(member.id)
        log.info("AccessToken 및 RefreshToken 생성 완료 - email: {}", member.email)

        // Refresh Token을 Redis에 저장
        redisTemplate.opsForValue()
            .set("RT:${member.id}", refreshToken, 7, TimeUnit.DAYS)

        log.info("로그인 성공 - email: {}", member.email)
        return mapOf(
            "accessToken" to accessToken,
            "refreshToken" to refreshToken
        )
    }

    @Transactional
    fun refreshToken(refreshToken: String, request: HttpServletRequest, response: HttpServletResponse): String {
        try {
            if (!jwtTokenProvider.validateToken(refreshToken)) {
                log.warn("유효하지 않은 Refresh Token 감지 - 자동 로그아웃 처리")
                logout(request, response) // 강제 로그아웃 처리
                throw CustomException(ErrorCode.INVALID_TOKEN)
            }

            // Refresh Token에서 userId 추출
            val userId = jwtTokenProvider.getUserIdFromRefreshToken(refreshToken)

            // Redis에 저장된 Refresh Token 확인
            val storedRefreshToken = redisTemplate.opsForValue().get("RT:$userId")
            if (storedRefreshToken == null || storedRefreshToken != refreshToken) {
                log.warn("🚨 저장된 Refresh Token과 일치하지 않음 - userId: {}", userId)
                throw CustomException(ErrorCode.INVALID_REFRESH_TOKEN)
            }

            // Member 정보 조회
            val member = memberRepository.findById(userId)
                .orElseThrow { CustomException(ErrorCode.MEMBER_NOT_FOUND) }

            // 새로운 Access Token 생성
            val authentication = UsernamePasswordAuthenticationToken(
                CustomUserDetails(member),
                null,
                setOf(SimpleGrantedAuthority(member.role.name))
            )

            return jwtTokenProvider.createAccessToken(authentication)

        } catch (e: CustomException) {
            throw e
        } catch (e: Exception) {
            log.error("AccessToken 재발급 실패: {}", e.message)
            throw CustomException(ErrorCode.TOKEN_REFRESH_FAILED)
        }
    }

    // RefreshToken Redis에서 유효한지 체크
    private fun validateRefreshTokenInRedis(userId: Long) {
        val refreshTokenKey = "RT:$userId"
        val storedRefreshToken = redisTemplate.opsForValue().get(refreshTokenKey)

        if (storedRefreshToken == null) {
            log.warn("Refresh Token 없음: userId={}", userId)
            throw CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND)
        }
    }

    // Redis에 있는 refreshToken 삭제
    private fun deleteRefreshToken(userId: Long) {
        val refreshTokenKey = "RT:$userId"
        val deleted = redisTemplate.delete(refreshTokenKey)

        if (deleted) {
            log.info("사용자 {}의 리프레시 토큰 삭제 완료 (자동 로그아웃 처리)", userId)
        } else {
            log.warn("리프레시 토큰 삭제 실패: userId={}", userId)
        }
    }
}
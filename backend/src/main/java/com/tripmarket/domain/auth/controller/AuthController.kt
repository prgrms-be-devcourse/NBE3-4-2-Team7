package com.tripmarket.domain.auth.controller

import com.tripmarket.domain.auth.dto.LoginRequestDto
import com.tripmarket.domain.auth.dto.SignupRequestDto
import com.tripmarket.domain.auth.service.AuthService
import com.tripmarket.global.util.CookieUtil
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 인증 관련 요청을 처리하는 컨트롤러
 * 토큰 재발급과 로그아웃 기능을 제공
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "Auth", description = "Auth API")
class AuthController(
    private val authService: AuthService,
    private val cookieUtil: CookieUtil
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @PostMapping("/signup")
    @Operation(summary = "일반 회원가입")
    fun signUp(@Valid @RequestBody signUpRequestDto: SignupRequestDto): ResponseEntity<String> {
        authService.signUp(signUpRequestDto)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @PostMapping("/login")
    @Operation(summary = "일반 로그인")
    fun login(
        @Valid @RequestBody loginRequestDto: LoginRequestDto,
        response: HttpServletResponse
    ): ResponseEntity<String> {
        log.debug("로그인 시도 - email: {}", loginRequestDto.email)

        // 로그인 처리 및 토큰 발급
        val tokens = authService.login(loginRequestDto)

        // Access Token 쿠키 설정
        val accessTokenCookie = cookieUtil.createAccessTokenCookie(tokens["accessToken"])
        val refreshTokenCookie = cookieUtil.createRefreshTokenCookie(tokens["refreshToken"])

        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())

        log.debug("로그인 성공 - email: {}", loginRequestDto.email)
        return ResponseEntity.status(HttpStatus.OK).build()
    }

    /**
     * Access Token 재발급
     * 1. 만료된 Access Token으로부터 사용자 정보 추출
     * 2. Redis에서 Refresh Token 유효성 검증
     * 3. 새로운 Access Token 발급
     */
    @PostMapping("/refresh")
    @Operation(summary = "Access Token 재발급")
    fun refreshToken(request: HttpServletRequest, response: HttpServletResponse): ResponseEntity<String> {
        // 1. 전달받은 쿠키에서 JWT 추출
        val refreshTokenFromCookie = cookieUtil.extractRefreshTokenFromCookie(request)

        // 2. 신규 AccessToken 발급
        val newAccessToken = authService.refreshToken(refreshTokenFromCookie, request, response)

        // 3. 발급한 AccessToken을 쿠키에 저장 및 response
        val accessTokenCookie = cookieUtil.createAccessTokenCookie(newAccessToken)
        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())

        return ResponseEntity.status(HttpStatus.OK).build()
    }

    /**
     * 로그아웃
     * 1. Access Token 블랙리스트 추가
     * 2. Redis에서 Refresh Token 삭제
     * 3. 쿠키 삭제
     */
    @PostMapping("/logout")
    @Operation(summary = "로그아웃")
    fun logout(request: HttpServletRequest, response: HttpServletResponse): ResponseEntity<String> {
        authService.logout(request, response)
        log.info("로그아웃 성공")
        return ResponseEntity.status(HttpStatus.OK).build()
    }
}
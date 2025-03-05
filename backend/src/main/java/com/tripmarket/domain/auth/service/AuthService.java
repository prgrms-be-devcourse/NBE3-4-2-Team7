package com.tripmarket.domain.auth.service;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tripmarket.domain.auth.dto.LoginRequestDto;
import com.tripmarket.domain.auth.dto.SignUpRequestDto;
import com.tripmarket.domain.member.entity.Member;
import com.tripmarket.domain.member.repository.MemberRepository;
import com.tripmarket.global.exception.CustomException;
import com.tripmarket.global.exception.ErrorCode;
import com.tripmarket.global.exception.JwtAuthenticationException;
import com.tripmarket.global.jwt.JwtTokenProvider;
import com.tripmarket.global.security.CustomUserDetails;
import com.tripmarket.global.util.CookieUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

	private final JwtTokenProvider jwtTokenProvider;
	private final RedisTemplate<String, String> redisTemplate;
	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;
	private final CookieUtil cookieUtil;

	public void logout(HttpServletRequest request, HttpServletResponse response) {
		log.debug("로그아웃 요청 수신");

		String logoutRefreshToken = cookieUtil.extractRefreshTokenFromCookie(request);
		String logoutAccessToken = cookieUtil.extractAccessTokenFromCookie(request);

		try {
			// 1. RefreshToken 에서 사용자 ID 추출
			Long userId = jwtTokenProvider.getUserIdFromRefreshToken(logoutRefreshToken);

			// 2. AccessToken 이 유효하면 블랙리스트 추가
			if (jwtTokenProvider.validateToken(logoutAccessToken)) {
				jwtTokenProvider.addToBlacklist(logoutAccessToken);
				log.info("AccessToken 블랙리스트에 추가 완료 - userId: {}", userId);
			}

			// 3. Refresh Token 유효한지 검증
			validateRefreshToken(userId);

			// 4. Redis에서 리프레시 토큰 삭제
			deleteRefreshToken(userId);

			// 5. 쿠키 삭제
			ResponseCookie emptyAccessCookie = cookieUtil.createLogoutAccessCookie();
			ResponseCookie emptyRefreshCookie = cookieUtil.createLogoutRefreshCookie();

			response.addHeader(HttpHeaders.SET_COOKIE, emptyAccessCookie.toString());
			response.addHeader(HttpHeaders.SET_COOKIE, emptyRefreshCookie.toString());

		} catch (Exception e) {
			log.error("로그아웃 처리 중 오류 발생", e);
			throw new CustomException(ErrorCode.LOGOUT_FAILED);
		}
	}

	@Transactional
	public void signUp(SignUpRequestDto signUpRequestDto) {
		// 중복 검사
		if (memberRepository.findByEmail(signUpRequestDto.email()).isPresent()) {
			throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
		}

		Member member = Member.createNormalMember(signUpRequestDto, passwordEncoder);
		memberRepository.save(member);
	}

	@Transactional
	public Map<String, String> login(LoginRequestDto loginRequestDto) {
		log.info("로그인 요청 - email: {}", loginRequestDto.email());

		// 1. 회원 존재 여부 확인
		Member member = memberRepository.findByEmail(loginRequestDto.email())
			.orElseThrow(() -> {
				log.warn("로그인 실패 - 존재하지 않는 이메일: {}", loginRequestDto.email());
				return new CustomException(ErrorCode.MEMBER_NOT_FOUND);
			});

		// 2. 비밀번호 확인
		if (!passwordEncoder.matches(loginRequestDto.password(), member.getPassword())) {
			log.warn("로그인 실패 - 잘못된 비밀번호: {}", loginRequestDto.email());
			throw new CustomException(ErrorCode.INVALID_PASSWORD);
		}

		// 3. CustomUserDetails를 사용한 인증 객체 생성
		CustomUserDetails userDetails = new CustomUserDetails(member);
		Authentication authentication = new UsernamePasswordAuthenticationToken(
			userDetails, // principal을 CustomUserDetails로 변경
			null, // credentials
			userDetails.getAuthorities() // authorities도 CustomUserDetails에서 가져옴
		);

		// 4. JWT 토큰 생성
		String accessToken = jwtTokenProvider.createAccessToken(authentication);
		String refreshToken = jwtTokenProvider.createRefreshToken(member.getId());
		log.info("AccessToken 및 RefreshToken 생성 완료 - email: {}", member.getEmail());

		// 5. Refresh Token을 Redis에 저장
		redisTemplate.opsForValue()
			.set("RT:" + member.getId(), refreshToken, 7, TimeUnit.DAYS);

		log.info("로그인 성공 - email: {}", member.getEmail());
		return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
	}

	@Transactional
	public String refreshToken(String refreshToken) {
		try {
			// 1. 리프레시 토큰 유효성 검사
			jwtTokenProvider.validateToken(refreshToken);

			// 2. Refresh Token에서 userId 추출
			Long userId = jwtTokenProvider.getUserIdFromRefreshToken(refreshToken);

			// 3. Redis에 저장된 Refresh Token 확인
			String storedRefreshToken = redisTemplate.opsForValue().get("RT:" + userId);
			if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
				log.warn("🚨 저장된 Refresh Token과 일치하지 않음 - userId: {}", userId);
				throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
			}

			// 4. Member 정보 조회
			Member member = memberRepository.findById(userId)
				.orElseThrow(() -> new JwtAuthenticationException("사용자를 찾을 수 없습니다."));

			// 5. 새로운 Access Token 생성
			Authentication authentication = new UsernamePasswordAuthenticationToken(
				new CustomUserDetails(member),
				null,
				Collections.singleton(new SimpleGrantedAuthority(member.getRole().name())));

			return jwtTokenProvider.createAccessToken(authentication);

		} catch (Exception e) {
			log.error("AccessToken 재발급 실패: {}", e.getMessage());
			throw new CustomException(ErrorCode.TOKEN_REFRESH_FAILED);
		}
	}

	// RefreshToken Redis에서 유효한지 체크
	private void validateRefreshToken(Long userId) {
		String refreshTokenKey = "RT:" + userId;
		String storedRefreshToken = redisTemplate.opsForValue().get(refreshTokenKey);

		if (storedRefreshToken == null) {
			log.warn("Refresh Token 없음: userId={}", userId);
			throw new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
		}
	}

	// Redis에 있는 refreshToken 삭제
	private void deleteRefreshToken(Long userId) {
		String refreshTokenKey = "RT:" + userId;
		Boolean deleted = redisTemplate.delete(refreshTokenKey);

		if (Boolean.TRUE.equals(deleted)) {
			log.info("사용자 {}의 리프레시 토큰 삭제 완료 (자동 로그아웃 처리)", userId);
		} else {
			log.warn("리프레시 토큰 삭제 실패: userId={}", userId);
		}
	}
}

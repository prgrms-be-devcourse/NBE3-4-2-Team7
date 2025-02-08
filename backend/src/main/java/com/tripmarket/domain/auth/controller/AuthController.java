package com.tripmarket.domain.auth.controller;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tripmarket.global.exception.JwtAuthenticationException;
import com.tripmarket.global.jwt.JwtTokenProvider;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 인증 관련 요청을 처리하는 컨트롤러
 * 토큰 재발급과 로그아웃 기능을 제공
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Auth", description = "Auth API")
public class AuthController {

	private final JwtTokenProvider jwtTokenProvider;
	private final RedisTemplate<String, String> redisTemplate;

	/**
	 * Access Token 재발급
	 * 1. 만료된 Access Token으로부터 사용자 정보 추출
	 * 2. Redis에서 Refresh Token 유효성 검증
	 * 3. 새로운 Access Token 발급
	 *
	 * @param request HTTP 요청 (만료된 Access Token이 쿠키에 포함되어 있어야 함)
	 * @param response HTTP 응답 (새로운 Access Token을 쿠키에 설정)
	 * @throws JwtAuthenticationException 토큰 재발급 실패 시
	 */
	@PostMapping("/refresh")
	@Operation(summary = "토큰 재발급")
	public void refreshToken(HttpServletRequest request, HttpServletResponse response) {
		String accessToken = jwtTokenProvider.resolveToken(request);

		try {
			// 1. Access Token 블랙리스트 체크
			if (jwtTokenProvider.isBlacklisted(accessToken)) {
				throw new JwtAuthenticationException("유효하지 않은 토큰입니다.");
			}

			// 2. 토큰에서 사용자 정보 추출
			Long userId = jwtTokenProvider.getUserIdFromExpiredToken(accessToken);
			Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);

			// 3. Redis에서 Refresh Token 조회 및 검증
			String refreshToken = redisTemplate.opsForValue().get("RT:" + userId);
			if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
				throw new JwtAuthenticationException("Refresh Token이 유효하지 않습니다.");
			}

			// 4. 기존 Access Token을 블랙리스트에 추가
			jwtTokenProvider.addToBlacklist(accessToken);

			// 5. 새로운 Access Token 발급
			String newAccessToken = jwtTokenProvider.createAccessToken(authentication);

			// 6. 새로운 Access Token을 쿠키에 저장
			ResponseCookie accessTokenCookie = jwtTokenProvider.createAccessTokenCookie(newAccessToken);
			response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
			log.info("Token refreshed for user: {}", userId);

		} catch (Exception e) {
			log.error("Token refresh failed", e);
			throw new JwtAuthenticationException("토큰 갱신 실패");
		}
	}

	/**
	 * 로그아웃
	 * 1. Access Token 블랙리스트 추가
	 * 2. Redis에서 Refresh Token 삭제
	 * 3. 쿠키 삭제
	 *
	 * @param request HTTP 요청 (현재 Access Token이 쿠키에 포함되어 있어야 함)
	 * @param response HTTP 응답 (쿠키 삭제)
	 * @throws JwtAuthenticationException 로그아웃 실패 시
	 */
	@PostMapping("/logout")
	@Operation(summary = "로그아웃")
	public void logout(HttpServletRequest request, HttpServletResponse response) {
		String accessToken = jwtTokenProvider.resolveToken(request);

		try {
			// 1. 블랙리스트 체크
			if (jwtTokenProvider.isBlacklisted(accessToken)) {
				throw new JwtAuthenticationException("이미 로그아웃된 토큰입니다.");
			}

			// 2. 토큰에서 사용자 정보 추출
			Long userId = jwtTokenProvider.getUserIdFromExpiredToken(accessToken);

			// 3. Access Token 블랙리스트 추가
			jwtTokenProvider.addToBlacklist(accessToken);

			// 4. Refresh Token 삭제
			redisTemplate.delete("RT:" + userId);

			// 5. 쿠키 삭제
			ResponseCookie emptyCookie = jwtTokenProvider.createEmptyCookie();
			response.addHeader(HttpHeaders.SET_COOKIE, emptyCookie.toString());

			log.info("Logout successful for user: {}", userId);
		} catch (Exception e) {
			log.error("Logout failed", e);
			throw new JwtAuthenticationException("로그아웃 실패");
		}
	}
}

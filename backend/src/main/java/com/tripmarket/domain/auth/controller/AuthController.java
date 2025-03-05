package com.tripmarket.domain.auth.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tripmarket.domain.auth.service.AuthService;
import com.tripmarket.global.exception.JwtAuthenticationException;
import com.tripmarket.global.jwt.JwtTokenProvider;
import com.tripmarket.global.util.CookieUtil;

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
	private final AuthService authService;
	private final CookieUtil cookieUtil;

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
	@Operation(summary = "AccessToken 재발급")
	public void refreshToken(HttpServletRequest request, HttpServletResponse response) {
		String accessToken = cookieUtil.extractTokenFromCookie(request);

		if (accessToken == null) {
			log.error("토큰이 요청에 없습니다(auth/refresh)");
			throw new JwtAuthenticationException("토큰이 존재하지 않습니다.");
		}

		try {
			String newAccessToken = authService.refreshAccessToken(accessToken);

			// 새로운 Access Token을 쿠키에 저장
			ResponseCookie cookie = cookieUtil.createAccessTokenCookie(newAccessToken);
			response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

			log.debug("토큰 갱신 성공");
		} catch (Exception e) {
			log.warn("AccessToken refresh failed: {}", e.getMessage());
			throw new JwtAuthenticationException("토큰 갱신 실패: " + e.getMessage());
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
	public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
		String accessToken = cookieUtil.extractTokenFromCookie(request);

		if (accessToken == null) {
			log.error("토큰이 요청에 없습니다(auth/logout)");
			throw new JwtAuthenticationException("토큰이 존재하지 않습니다.");
		}

		try {
			authService.logout(accessToken);
			// 쿠키 삭제
			ResponseCookie emptyCookie = cookieUtil.createLogoutCookie();
			response.addHeader(HttpHeaders.SET_COOKIE, emptyCookie.toString());

			log.debug("로그아웃 성공");
			return ResponseEntity.ok("로그아웃이 성공적으로 완료되었습니다.");
		} catch (Exception e) {
			log.error("로그아웃 실패: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("로그아웃 실패");
		}
	}
}
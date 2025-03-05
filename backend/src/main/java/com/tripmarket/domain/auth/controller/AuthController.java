package com.tripmarket.domain.auth.controller;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tripmarket.domain.auth.dto.LoginRequestDto;
import com.tripmarket.domain.auth.dto.SignUpRequestDto;
import com.tripmarket.domain.auth.service.AuthService;
import com.tripmarket.global.exception.JwtAuthenticationException;
import com.tripmarket.global.util.CookieUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
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

	private final AuthService authService;
	private final CookieUtil cookieUtil;

	@PostMapping("/signup")
	@Operation(summary = "일반 회원가입")
	public ResponseEntity<String> signUp(@Valid @RequestBody SignUpRequestDto signUpRequestDto) {
		authService.signUp(signUpRequestDto);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PostMapping("/login")
	@Operation(summary = "일반 로그인")
	public ResponseEntity<String> login(
		@Valid @RequestBody LoginRequestDto loginRequestDto,
		HttpServletResponse response) {
		log.debug("로그인 시도 - email: {}", loginRequestDto.email());

		// 로그인 처리 및 토큰 발급
		Map<String, String> tokens = authService.login(loginRequestDto);

		// Access Token 쿠키 설정
		ResponseCookie accessTokenCookie = cookieUtil.createAccessTokenCookie(tokens.get("accessToken"));
		ResponseCookie refreshTokenCookie = cookieUtil.createRefreshTokenCookie(tokens.get("refreshToken"));

		response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
		response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

		log.debug("로그인 성공 - email: {}", loginRequestDto.email());
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	/**
	 * Access Token 재발급
	 * 1. 만료된 Access Token으로부터 사용자 정보 추출
	 * 2. Redis에서 Refresh Token 유효성 검증
	 * 3. 새로운 Access Token 발급
	 *
	 * @param request  HTTP 요청 (만료된 Access Token이 쿠키에 포함되어 있어야 함)
	 * @param response HTTP 응답 (새로운 Access Token을 쿠키에 설정)
	 * @throws JwtAuthenticationException 토큰 재발급 실패 시
	 */
	@PostMapping("/refresh")
	@Operation(summary = "Access Token 재발급")
	public ResponseEntity<String> refreshToken(HttpServletRequest request, HttpServletResponse response) {
		// 1. 전달받은 쿠키에서 JWT 추출
		String refreshToken = cookieUtil.extractRefreshTokenFromCookie(request);

		// 2. 신규 AccessToken 발급
		String newAccessToken = authService.refreshToken(refreshToken, request, response);

		// 3. 발급한 AccessToken 을 쿠키에 저장 및 response
		ResponseCookie accessTokenCookie = cookieUtil.createAccessTokenCookie(newAccessToken);
		response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());

		return ResponseEntity.status(HttpStatus.OK).build();
	}

	/**
	 * 로그아웃
	 * 1. Access Token 블랙리스트 추가
	 * 2. Redis에서 Refresh Token 삭제
	 * 3. 쿠키 삭제
	 *
	 * @param request  HTTP 요청 (현재 Access Token이 쿠키에 포함되어 있어야 함)
	 * @param response HTTP 응답 (쿠키 삭제)
	 * @throws JwtAuthenticationException 로그아웃 실패 시
	 */
	@PostMapping("/logout")
	@Operation(summary = "로그아웃")
	public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
		authService.logout(request, response);
		log.info("로그아웃 성공");
		return ResponseEntity.status(HttpStatus.OK).build();
	}
}

package com.tripmarket.global.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class CookieUtil {

	@Value("${jwt.access-token-expire-time-seconds}")
	private long accessTokenValidityInSeconds;

	@Value("${jwt.refresh-token-expire-time-seconds}")
	private long refreshTokenValidityInSeconds;

	private static final String ACCESS_TOKEN_KEY = "accessToken";
	private static final String REFRESH_TOKEN_KEY = "refreshToken";

	/**
	 * HTTP 요청의 쿠키에서 JWT 토큰 추출
	 *
	 * @param request HTTP 요청
	 * @return 쿠키에서 추출한 토큰, 없으면 null
	 */

	// Access Token 추출을 위한 메서드
	public String extractAccessTokenFromCookie(HttpServletRequest request) {
		return extractCookieValue(request, ACCESS_TOKEN_KEY);
	}

	// Refresh Token 추출을 위한 메서드
	public String extractRefreshTokenFromCookie(HttpServletRequest request) {
		return extractCookieValue(request, REFRESH_TOKEN_KEY);
	}

	// 공용 토큰 추출 메서드
	private String extractCookieValue(HttpServletRequest request, String cookieName) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookieName.equals(cookie.getName())) {
					log.info("쿠키에서 {} 추출 완료", cookieName);
					return cookie.getValue();
				}
			}
		}
		return null;
	}

	/**
	 * Access Token을 담은 쿠키 생성
	 *
	 * @param token JWT 토큰
	 * @return 설정된 쿠키 객체
	 */
	public ResponseCookie createAccessTokenCookie(String token) {
		ResponseCookie cookie = ResponseCookie.from(ACCESS_TOKEN_KEY, token)
			.httpOnly(false) // JavaScript에서 접근 불가
			.secure(false) // HTTPS에서만 전송, localhost에서는 false로 설정해야 함
			.sameSite("Lax") // CSRF 방지
			.path("/") // 모든 경로에서 접근 가능
			.maxAge(accessTokenValidityInSeconds)
			.build();

		log.info("Access Token 쿠키 생성 완료");
		return cookie;
	}

	public ResponseCookie createRefreshTokenCookie(String refreshToken) {
		ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_KEY, refreshToken)
			.httpOnly(false)
			.secure(false)
			.sameSite("Lax")
			.path("/")
			.maxAge(refreshTokenValidityInSeconds)
			.build();

		log.info("Refresh Token 쿠키 생성 완료");
		return cookie;
	}

	/**
	 * 로그아웃 시 쿠키 삭제를 위한 빈 쿠키 생성
	 *
	 * @return 만료된 쿠키 객체
	 */
	public ResponseCookie createLogoutAccessCookie() {
		return ResponseCookie.from(ACCESS_TOKEN_KEY, "")
			.httpOnly(false)
			.secure(false)
			.sameSite("Lax")
			.path("/")
			.maxAge(0) // 즉시 만료
			.build();
	}

	public ResponseCookie createLogoutRefreshCookie() {
		return ResponseCookie.from(REFRESH_TOKEN_KEY, "")
			.httpOnly(false)
			.secure(false)
			.sameSite("Lax")
			.path("/")
			.maxAge(0) // 즉시 만료
			.build();
	}
}

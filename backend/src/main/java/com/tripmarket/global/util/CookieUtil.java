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

	@Value("${jwt.refresh-token-expire-time-seconds}")
	private long refreshTokenValidityInSeconds;

	/**
	 * HTTP 요청의 쿠키에서 JWT 토큰 추출
	 *
	 * @param request HTTP 요청
	 * @return 쿠키에서 추출한 토큰, 없으면 null
	 */
	public String extractTokenFromCookie(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("accessToken".equals(cookie.getName())) {
					log.debug("쿠키에서 accessToken 추출: {}", cookie.getValue());
					return cookie.getValue();
				}
			}
		}
		log.debug("쿠키에서 accessToken 추출 못함");
		return null;
	}

	/**
	 * Access Token을 담은 쿠키 생성
	 *
	 * @param token JWT 토큰
	 * @return 설정된 쿠키 객체
	 */
	public ResponseCookie createAccessTokenCookie(String token) {
		ResponseCookie cookie = ResponseCookie.from("accessToken", token)
			.httpOnly(true)    // JavaScript에서 접근 불가
			.secure(false)      // HTTPS에서만 전송, localhost에서는 false로 설정해야 함
			.sameSite("Lax")   // CSRF 방지
			.path("/")         // 모든 경로에서 접근 가능
			.maxAge(refreshTokenValidityInSeconds)
			.build();

		log.debug("Access Token 쿠키 생성: {}", cookie);
		return cookie;
	}

	/**
	 * 로그아웃 시 쿠키 삭제를 위한 빈 쿠키 생성
	 *
	 * @return 만료된 쿠키 객체
	 */
	public ResponseCookie createLogoutCookie() {
		return ResponseCookie.from("accessToken", "")
			.httpOnly(true)
			.secure(false)
			.sameSite("Lax")
			.path("/")
			.maxAge(0)  // 즉시 만료
			.build();
	}
}

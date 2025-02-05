package com.tripmarket.global.jwt;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		// 쿠키에서 JWT 토큰 추출
		String token = resolveToken(request);
		log.debug("Resolved token: {}", token);

		// validateToken으로 토큰 유효성 검사
		if (token != null && jwtTokenProvider.validateToken(token)) {
			// 토큰이 유효할 경우 토큰에서 Authentication 객체를 가지고 와서 SecurityContext에 저장
			Authentication authentication = jwtTokenProvider.getAuthentication(token);
			log.debug("Authentication: {}", authentication);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			log.debug("Security Context에 '{}' 인증 정보를 저장했습니다.", authentication.getName());
		}
		filterChain.doFilter(request, response);
	}

	/**
	 * 쿠키에서 토큰 정보 추출
	 * @param request HTTP 요청
	 * @return 쿠키에서 추출한 토큰 값
	 */
	private String resolveToken(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("accessToken".equals(cookie.getName())) {
					log.debug("Found accessToken in cookie: {}", cookie.getValue());
					return cookie.getValue();
				}
			}
		}
		log.debug("No cookie found in request");
		return null;
	}
}

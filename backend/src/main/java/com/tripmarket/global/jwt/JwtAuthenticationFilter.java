package com.tripmarket.global.jwt;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.tripmarket.global.exception.JwtAuthenticationException;
import com.tripmarket.global.util.CookieUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * JWT 토큰 기반 인증을 처리하는 필터
 * 모든 요청에 대해 JWT 토큰을 검증하고 인증 정보를 설정
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;
	private final CookieUtil cookieUtil;

	/**
	 * 실제 필터링 로직
	 * 1. 쿠키에서 토큰 추출
	 * 2. 블랙리스트 확인
	 * 3. 토큰 유효성 검증
	 * 4. 인증 정보 설정
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		try {
			// 쿠키에서 JWT 토큰 추출
			String accessToken = cookieUtil.extractTokenFromCookie(request);
			log.debug("JwtAuthenticationFilter - 쿠키에서 accessToken 추출: {}", accessToken);

			if (accessToken != null) {
				// Access Token 블랙리스트 확인
				if (jwtTokenProvider.isBlacklisted(accessToken)) {
					log.warn("JwtAuthenticationFilter - 블랙리스트에 등록된 accessToken");
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
					return;
				}

				try {
					// /auth/refresh 요청이 아닌 경우에만 토큰 유효성 검증
					if (!request.getRequestURI().equals("/auth/refresh")) {
						if (jwtTokenProvider.validateToken(accessToken)) {
							Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
							SecurityContextHolder.getContext().setAuthentication(authentication);
							log.debug("사용자 '{}'의 인증 정보를 security context에 설정함", authentication.getName());
						}
					}
				} catch (JwtAuthenticationException e) {
					// /auth/refresh 요청이 아닌 경우에만 예외 처리
					if (!request.getRequestURI().equals("/auth/refresh")) {
						throw e;
					}
				}
			}

			filterChain.doFilter(request, response);

		} catch (Exception e) {
			log.debug("사용자 인증 정보를 설정할 수 없음: {}", e.getMessage());
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
	}
}
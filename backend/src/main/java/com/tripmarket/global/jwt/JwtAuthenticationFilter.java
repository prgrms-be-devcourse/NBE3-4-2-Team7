package com.tripmarket.global.jwt;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.tripmarket.global.exception.JwtAuthenticationException;

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
			String token = jwtTokenProvider.resolveToken(request);
			log.debug("Resolved token: {}", token);

			if (token != null) {
				// Access Token 블랙리스트 확인
				if (jwtTokenProvider.isBlacklisted(token)) {
					log.debug("Token is blacklisted");
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
					return;
				}

				try {
					// /auth/refresh 요청이 아닌 경우에만 토큰 유효성 검증
					if (!request.getRequestURI().equals("/auth/refresh")) {
						if (jwtTokenProvider.validateToken(token)) {
							Authentication authentication = jwtTokenProvider.getAuthentication(token);
							SecurityContextHolder.getContext().setAuthentication(authentication);
							log.debug("Set Authentication to security context for user: {}", authentication.getName());
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
			log.error("Cannot set user authentication: {}", e.getMessage());
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
	}
}
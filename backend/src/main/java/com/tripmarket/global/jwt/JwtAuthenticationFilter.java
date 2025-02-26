package com.tripmarket.global.jwt;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
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

	// 불필요한 인증 실행을 막는 코드
	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String path = request.getRequestURI();

		// SecurityConfig의 permitAll()과 일치하는 경로들
		return path.startsWith("/h2-console") ||
				path.startsWith("/swagger-ui") ||
				path.startsWith("/api-docs") ||
				path.startsWith("/chat-test.html") ||
				path.startsWith("/chat") ||
				path.equals("/") ||
				path.startsWith("/auth") ||
				path.startsWith("/oauth2") ||
				path.startsWith("/login/auth") ||
				path.startsWith("/login/oauth2");
	}

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
			String accessToken = cookieUtil.extractAccessTokenFromCookie(request);

			if (accessToken != null && !jwtTokenProvider.isBlacklisted(accessToken)) {
				try {
					jwtTokenProvider.validateToken(accessToken);
					Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
					SecurityContextHolder.getContext().setAuthentication(authentication);
				} catch (JwtAuthenticationException e) {
					// 토큰이 만료된 경우 401 응답
					if (e.getMessage().equals("만료된 JWT 토큰입니다.")) {
						response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
						response.setContentType("application/json");
						response.getWriter().write("{\"message\": \"만료된 토큰입니다.\", \"code\": \"TOKEN_EXPIRED\"}");
						return;
					}
					throw e;
				}
			}

			filterChain.doFilter(request, response);
		} catch (Exception e) {
			log.error("Could not set user authentication in security context", e);
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().write("Unauthorized: " + e.getMessage());
		}
	}
}
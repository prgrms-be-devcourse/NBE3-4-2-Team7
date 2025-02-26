package com.tripmarket.global.oauth2.handler;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.tripmarket.global.jwt.JwtTokenProvider;
import com.tripmarket.global.oauth2.CustomOAuth2User;
import com.tripmarket.global.util.CookieUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * OAuth2 인증 성공 시 처리를 담당하는 핸들러
 * 토큰 생성 및 쿠키 설정, 리다이렉트를 처리
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private final JwtTokenProvider jwtTokenProvider;
	private final RedisTemplate<String, String> redisTemplate;
	private final CookieUtil cookieUtil;

	@Value("${jwt.refresh-token-expire-time-seconds}")
	private long refreshTokenValidityInSeconds;

	@Value("${spring.security.oauth2.authorized-redirect-uri}")
	private String redirectUri;

	/**
	 * OAuth2 인증 성공 시 실행되는 메서드
	 * 1. Access Token 생성 및 쿠키 설정
	 * 2. Refresh Token 생성 및 Redis 저장
	 * 3. 프론트엔드로 리다이렉트
	 */
	@Override
	public void onAuthenticationSuccess(
			HttpServletRequest request,
			HttpServletResponse response,
			Authentication authentication) throws IOException {

		CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
		Long userId = oAuth2User.getId();

		log.debug("OAuth2 사용자 정보 - userId: {}, email: {}", userId, oAuth2User.getEmail());

		// Access Token 생성 및 쿠키에 설정 (30분)
		String accessToken = jwtTokenProvider.createAccessToken(authentication);
		ResponseCookie accessTokenCookie = cookieUtil.createAccessTokenCookie(accessToken);

		// Refresh Token 생성 및 Redis 저장, 쿠키에 설정 (7일)
		String refreshToken = jwtTokenProvider.createRefreshToken(userId);
		ResponseCookie refreshTokenCookie = cookieUtil.createRefreshTokenCookie(refreshToken);
		redisTemplate.opsForValue()
				.set("RT:" + userId, refreshToken, refreshTokenValidityInSeconds, TimeUnit.SECONDS);

		// 쿠키 추가
		response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
		response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

		log.info("OAuth2 Login Success: userId-{}", userId);

		// 성공 상태와 함께 리다이렉트
		String targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
				.queryParam("status", "success")
				.build().toUriString();

		getRedirectStrategy().sendRedirect(request, response, targetUrl);
	}
}

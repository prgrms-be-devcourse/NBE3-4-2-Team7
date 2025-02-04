package com.tripmarket.global.oauth2.handler;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.tripmarket.global.jwt.JwtTokenProvider;
import com.tripmarket.global.oauth2.CustomOAuth2User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private final JwtTokenProvider jwtTokenProvider;
	private final RedisTemplate<String, String> redisTemplate;
	private static final String REDIRECT_URI = "http://localhost:3000/";

	@Override
	public void onAuthenticationSuccess(
		HttpServletRequest request,
		HttpServletResponse response,
		Authentication authentication) throws IOException {

		CustomOAuth2User oAuth2User = (CustomOAuth2User)authentication.getPrincipal();
		String email = oAuth2User.getEmail();

		// Access Token 생성 및 쿠키에 설정 (30분)
		String accessToken = jwtTokenProvider.createAccessToken(authentication);
		ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", accessToken)
			.httpOnly(true)
			.secure(true)
			.sameSite("Lax")
			.path("/")
			.maxAge(1800)
			.build();

		// Refresh Token 생성 및 Redis 저장 (7일)
		String refreshToken = jwtTokenProvider.createRefreshToken();
		redisTemplate.opsForValue()
			.set("RT:" + email, refreshToken, 604800, TimeUnit.SECONDS);

		// 쿠키 추가
		response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());

		log.info("OAuth2 Login Success: {}", email);

		// 프론트엔드로 리다이렉트
		getRedirectStrategy().sendRedirect(request, response, REDIRECT_URI);
	}
}

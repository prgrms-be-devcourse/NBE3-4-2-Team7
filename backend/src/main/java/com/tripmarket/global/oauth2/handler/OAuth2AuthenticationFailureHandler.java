package com.tripmarket.global.oauth2.handler;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * OAuth2 인증 실패 시 처리를 담당하는 핸들러
 */
@Slf4j
@Component
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	@Value("${spring.security.oauth2.authorized-redirect-uri}")
	private String redirectUri;

	/**
	 * OAuth2 인증 실패 시 실행되는 메서드
	 * 실패 상태와 함께 프론트엔드로 리다이렉트
	 */
	@Override
	public void onAuthenticationFailure(
		HttpServletRequest request,
		HttpServletResponse response,
		AuthenticationException exception) throws IOException {

		log.error("OAuth2 Login Failure: {}", exception.getMessage());

		// 실패 상태와 함께 리다이렉트
		String targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
			.queryParam("status", "fail")
			.build().toUriString();

		getRedirectStrategy().sendRedirect(request, response, targetUrl);
	}
}

package com.tripmarket.global.oauth2.service;

import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.tripmarket.domain.member.entity.Provider;
import com.tripmarket.global.exception.CustomException;
import com.tripmarket.global.exception.ErrorCode;
import com.tripmarket.global.oauth2.userinfo.GithubOAuth2UserInfo;
import com.tripmarket.global.oauth2.userinfo.GoogleOAuth2UserInfo;
import com.tripmarket.global.oauth2.userinfo.KakaoOAuth2UserInfo;
import com.tripmarket.global.oauth2.userinfo.OAuth2UserInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 소셜 토큰으로 사용자 정보를 조회하는 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OAuth2UserInfoService {
	private final RestTemplate restTemplate = new RestTemplate();

	/**
	 * 소셜 토큰으로 사용자 정보 조회
	 */
	public OAuth2UserInfo loadUserInfoByToken(Provider provider, String token) {
		return switch (provider) {
			case KAKAO -> loadKakaoUserByToken(token);
			case GOOGLE -> loadGoogleUserByToken(token);
			case GITHUB -> loadGithubUserByToken(token);
			default -> throw new CustomException(ErrorCode.UNSUPPORTED_SOCIAL_TYPE);
		};
	}

	/**
	 * 카카오 토큰으로 사용자 정보 조회
	 */
	private OAuth2UserInfo loadKakaoUserByToken(String token) {
		String userInfoEndpoint = "https://kapi.kakao.com/v2/user/me";

		try {
			Map<String, Object> response = restTemplate.exchange(
				userInfoEndpoint,
				HttpMethod.GET,
				new HttpEntity<>(createAuthHeaders(token)),
				new ParameterizedTypeReference<Map<String, Object>>() {}
			).getBody();

			return new KakaoOAuth2UserInfo(response);
		} catch (Exception e) {
			log.error("카카오 사용자 정보 조회 실패", e);
			throw new CustomException(ErrorCode.INVALID_SOCIAL_TOKEN);
		}
	}

	/**
	 * 구글 토큰으로 사용자 정보 조회
	 */
	private OAuth2UserInfo loadGoogleUserByToken(String token) {
		String userInfoEndpoint = "https://www.googleapis.com/oauth2/v3/userinfo";

		try {
			Map<String, Object> response = restTemplate.exchange(
				userInfoEndpoint,
				HttpMethod.GET,
				new HttpEntity<>(createAuthHeaders(token)),
				new ParameterizedTypeReference<Map<String, Object>>() {}
			).getBody();

			return new GoogleOAuth2UserInfo(response);
		} catch (Exception e) {
			log.error("구글 사용자 정보 조회 실패", e);
			throw new CustomException(ErrorCode.INVALID_SOCIAL_TOKEN);
		}
	}

	/**
	 * 깃허브 토큰으로 사용자 정보 조회
	 */
	private OAuth2UserInfo loadGithubUserByToken(String token) {
		String userInfoEndpoint = "https://api.github.com/user";

		try {
			Map<String, Object> response = restTemplate.exchange(
				userInfoEndpoint,
				HttpMethod.GET,
				new HttpEntity<>(createAuthHeaders(token)),
				new ParameterizedTypeReference<Map<String, Object>>() {}
			).getBody();

			return new GithubOAuth2UserInfo(response);
		} catch (Exception e) {
			log.error("깃허브 사용자 정보 조회 실패", e);
			throw new CustomException(ErrorCode.INVALID_SOCIAL_TOKEN);
		}
	}

	/**
	 * 인증 헤더 생성
	 */
	private HttpHeaders createAuthHeaders(String token) {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(token);
		return headers;
	}
}

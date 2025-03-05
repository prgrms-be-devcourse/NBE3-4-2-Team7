package com.tripmarket.global.oauth2.userinfo;

import java.util.Map;

/**
 * 카카오 OAuth2 사용자 정보를 처리하는 구현체
 * 카카오 API가 제공하는 사용자 정보 구조에 맞춰 정보를 추출
 */
public class KakaoOAuth2UserInfo extends OAuth2UserInfo {

	public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
		super(attributes);
	}

	@Override
	public String getId() {
		return String.valueOf(attributes.get("id")); // Long -> String 변환
	}

	@Override
	public String getEmail() {
		// kakao_account.email 형태로 응답
		Map<String, Object> account = (Map<String, Object>)attributes.get("kakao_account");
		return (String)account.get("email");
	}

	@Override
	public String getName() {
		// kakao_account.profile.nickname 형태로 응답
		Map<String, Object> account = (Map<String, Object>)attributes.get("kakao_account");
		Map<String, Object> profile = (Map<String, Object>)account.get("profile");
		return (String)profile.get("nickname");
	}

	@Override
	public String getImageUrl() {
		// kakao_account.profile.profile_image_url 형태로 응답
		Map<String, Object> account = (Map<String, Object>)attributes.get("kakao_account");
		Map<String, Object> profile = (Map<String, Object>)account.get("profile");
		return (String)profile.get("profile_image_url");
	}
}

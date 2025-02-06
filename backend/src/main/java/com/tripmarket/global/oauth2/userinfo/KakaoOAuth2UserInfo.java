package com.tripmarket.global.oauth2.userinfo;

import java.util.Map;

/**
 * 카카오 OAuth2 인증 결과로 받은 유저 정보를 매핑하는 클래스
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

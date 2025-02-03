package com.tripmarket.global.oauth2.userinfo;

import java.util.Map;

/**
 * 소셜 로그인 제공자별 유저 정보를 매핑하기 위한 추상 클래스
 */
public abstract class OAuth2UserInfo {
	protected Map<String, Object> attributes;

	public OAuth2UserInfo(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	public abstract String getId(); // 소셜 식별자

	public abstract String getEmail(); // 이메일

	public abstract String getName(); // 이름

	public abstract String getImageUrl(); // 프로필 이미지
}

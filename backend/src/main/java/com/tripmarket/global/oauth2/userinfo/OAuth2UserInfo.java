package com.tripmarket.global.oauth2.userinfo;

import java.util.Map;

/**
 * OAuth2 제공자별 사용자 정보를 표준화하기 위한 추상 클래스
 * 각 OAuth2 제공자(카카오, 구글 등)는 이 클래스를 구현하여 사용
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

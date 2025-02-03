package com.tripmarket.global.oauth2;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import lombok.Getter;

/**
 * OAuth2User를 상속받아 구현한 커스텀 유저 클래스
 * 소셜 로그인 시 사용할 추가 정보들을 담고 있음
 */
@Getter
public class CustomOAuth2User extends DefaultOAuth2User {

	private String email; // 이메일
	private String name; // 이름
	private String imageUrl; // 프로필 이미지 URL

	/**
	 * @param authorities 권한 정보
	 * @param attributes OAuth2 제공자로부터 받은 유저 정보
	 * @param nameAttributeKey OAuth2 제공자가 사용하는 유저 식별자의 키값
	 * @param email 유저 이메일
	 * @param name 유저 이름
	 * @param imageUrl 프로필 이미지 URL
	 */
	public CustomOAuth2User(
		Collection<? extends GrantedAuthority> authorities,
		Map<String, Object> attributes,
		String nameAttributeKey,
		String email,
		String name,
		String imageUrl
	) {
		super(authorities, attributes, nameAttributeKey);
		this.email = email;
		this.name = name;
		this.imageUrl = imageUrl;
	}
}

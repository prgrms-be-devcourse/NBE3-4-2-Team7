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

	private String email; // 사용자 식별용 이메일

	/**
	 * JWT 토큰 검증 후 인증 객체 생성을 위한 생성자
	 *
	 * @param authorities 사용자의 권한 정보 컬렉션 (예: ROLE_USER)
	 * @param attributes OAuth2 제공자로부터 받은 사용자 정보를 담은 Map (최소한 email은 포함)
	 * @param nameAttributeKey OAuth2 제공자가 사용하는 사용자 식별자의 키값 (예: "email")
	 * @param email 사용자 식별용 이메일
	 */
	public CustomOAuth2User(
		Collection<? extends GrantedAuthority> authorities,
		Map<String, Object> attributes,
		String nameAttributeKey,
		String email
	) {
		super(authorities, attributes, nameAttributeKey);
		this.email = email;
	}
}

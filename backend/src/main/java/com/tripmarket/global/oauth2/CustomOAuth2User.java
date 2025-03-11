package com.tripmarket.global.oauth2;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import lombok.Getter;

/**
 * OAuth2User를 확장한 커스텀 사용자 클래스
 * 기본 OAuth2User 정보 외에 우리 서비스의 사용자 ID를 추가로 보관
 */
@Getter
public class CustomOAuth2User extends DefaultOAuth2User {

	private final Long id; // 사용자 식별용 id (DB 사용자 id)

	private final String email; // 사용자 이메일

	/**
	 * JWT 토큰 검증 후 인증 객체 생성을 위한 생성자
	 *
	 * @param authorities      사용자의 권한 정보 컬렉션 (예: ROLE_USER)
	 * @param attributes       OAuth2 제공자로부터 받은 사용자 정보를 담은 Map (최소한 id는 포함)
	 * @param nameAttributeKey OAuth2 제공자가 사용하는 사용자 식별자의 키값 (예: "id")
	 * @param id               사용자 식별용 id
	 */
	public CustomOAuth2User(
		Collection<? extends GrantedAuthority> authorities,
		Map<String, Object> attributes,
		String nameAttributeKey,
		Long id,
		String email) {
		super(authorities, attributes, nameAttributeKey);
		this.id = id;
		this.email = email;
	}

	/**
	 * 코틀린에서 롬복 인식못해서 명시적으로 설정
	 * */
	public Long getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}
}

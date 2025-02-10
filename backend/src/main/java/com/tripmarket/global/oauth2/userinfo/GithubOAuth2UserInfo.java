package com.tripmarket.global.oauth2.userinfo;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 깃허브 OAuth2 사용자 정보를 처리하는 구현체
 * 깃허브 API가 제공하는 사용자 정보 구조에 맞춰 정보를 추출
 */
public class GithubOAuth2UserInfo extends OAuth2UserInfo {

	private static final Logger log = LoggerFactory.getLogger(GithubOAuth2UserInfo.class);

	public GithubOAuth2UserInfo(Map<String, Object> attributes) {
		super(attributes);
	}

	@Override
	public String getId() {
		return String.valueOf(attributes.get("id"));
	}

	@Override
	public String getEmail() {
		String email = (String) attributes.get("email");
		log.debug("GitHub email: {}", email);
		return email;
	}

	@Override
	public String getName() {
		String name = (String) attributes.get("name");
		return name != null ? name : (String) attributes.get("login");
	}

	@Override
	public String getImageUrl() {
		return (String) attributes.get("avatar_url");
	}
}

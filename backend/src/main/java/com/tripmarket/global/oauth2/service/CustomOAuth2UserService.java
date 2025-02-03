package com.tripmarket.global.oauth2.service;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.tripmarket.domain.member.entity.Member;
import com.tripmarket.domain.member.repository.MemberRepository;
import com.tripmarket.global.oauth2.CustomOAuth2User;
import com.tripmarket.global.oauth2.userinfo.KakaoOAuth2UserInfo;
import com.tripmarket.global.oauth2.userinfo.OAuth2UserInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

	private final MemberRepository memberRepository;

	/**
	 * OAuth2 인증 후 받아온 유저 정보를 처리
	 */
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
		OAuth2User oAuth2User = delegate.loadUser(userRequest);

		// 현재 진행중인 서비스를 구분하기 위해 문자열로 받음
		String registrationId = userRequest.getClientRegistration().getRegistrationId();

		// OAuth2 로그인 시 키가 되는 필드값 (PK)
		String usernameAttributeName = userRequest.getClientRegistration()
			.getProviderDetails()
			.getUserInfoEndpoint()
			.getUserNameAttributeName();

		// OAuth2UserInfo 객체 생성
		OAuth2UserInfo userInfo = new KakaoOAuth2UserInfo(oAuth2User.getAttributes());

		// 유저 정보 저장 또는 업데이트
		Member member = saveOrUpdate(userInfo);

		return new CustomOAuth2User(
			Collections.singleton(new SimpleGrantedAuthority(member.getRole().name())),
			oAuth2User.getAttributes(),
			usernameAttributeName,
			member.getEmail(),
			member.getName(),
			member.getImageUrl()
		);
	}

	private Member saveOrUpdate(OAuth2UserInfo userInfo) {
		Member member = memberRepository.findByEmail(userInfo.getEmail())
			.map(entity -> entity.updateOAuth2Profile(
				userInfo.getName(),
				userInfo.getImageUrl()
			))
			.orElse(Member.builder()
				.email(userInfo.getEmail())
				.name(userInfo.getName())
				.providerId(userInfo.getId())
				.imageUrl(userInfo.getImageUrl())
				.build());

		return memberRepository.save(member);
	}
}

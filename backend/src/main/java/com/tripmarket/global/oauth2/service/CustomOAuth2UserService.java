package com.tripmarket.global.oauth2.service;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tripmarket.domain.member.entity.Member;
import com.tripmarket.domain.member.entity.Provider;
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
	@Transactional
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
		Member member = saveOrUpdate(userInfo, registrationId);

		return new CustomOAuth2User(
			Collections.singleton(new SimpleGrantedAuthority(member.getRole().name())),
			oAuth2User.getAttributes(),
			usernameAttributeName,
			member.getEmail(),
			member.getName(),
			member.getImageUrl()
		);
	}

	/**
	 * OAuth2 유저 정보로 회원가입 또는 정보 업데이트
	 * @param userInfo OAuth2 제공자로부터 받은 유저 정보
	 * @param registrationId OAuth2 서비스 구분 ID (kakao, google 등)
	 */
	private Member saveOrUpdate(OAuth2UserInfo userInfo, String registrationId) {
		Provider provider = getProvider(registrationId);

		Member member = memberRepository.findByEmail(userInfo.getEmail())
			.map(entity -> entity.updateOAuth2Profile(
				userInfo.getName(),
				userInfo.getImageUrl()
			))
			.orElse(Member.builder()
				.email(userInfo.getEmail())
				.name(userInfo.getName())
				.providerId(userInfo.getId())
				.provider(provider)
				.imageUrl(userInfo.getImageUrl())
				.build());

		return memberRepository.save(member);
	}

	/**
	 * registrationId를 Provider enum으로 변환
	 * @param registrationId OAuth2 서비스 구분 ID (kakao, google 등)
	 * @throws OAuth2AuthenticationException 지원하지 않는 OAuth2 제공자일 경우
	 */
	private Provider getProvider(String registrationId) {
		try {
			return Provider.valueOf(registrationId.toUpperCase());
		} catch (IllegalArgumentException e) {
			log.error("Unsupported OAuth2 provider: {}", registrationId);
			throw new OAuth2AuthenticationException("Unsupported OAuth2 provider");
		}
	}
}

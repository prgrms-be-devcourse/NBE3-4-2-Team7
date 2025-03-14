package com.tripmarket.global.oauth2.service;

import java.util.Collections;
import java.util.Optional;

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
import com.tripmarket.domain.member.repository.SocialAccountLinkRepository;
import com.tripmarket.global.oauth2.CustomOAuth2User;
import com.tripmarket.global.oauth2.userinfo.GithubOAuth2UserInfo;
import com.tripmarket.global.oauth2.userinfo.GoogleOAuth2UserInfo;
import com.tripmarket.global.oauth2.userinfo.KakaoOAuth2UserInfo;
import com.tripmarket.global.oauth2.userinfo.OAuth2UserInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * OAuth2 인증 후 사용자 정보를 처리하는 서비스
 * 카카오 등 OAuth2 제공자로부터 받은 정보로 회원가입/로그인 처리
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

	private final MemberRepository memberRepository;
	private final SocialAccountLinkRepository socialAccountLinkRepository;

	/**
	 * OAuth2 인증 후 받아온 사용자 정보를 처리
	 * 1. 기본 OAuth2UserService로 사용자 정보 조회
	 * 2. 우리 서비스의 회원으로 가입 또는 정보 업데이트
	 * 3. CustomOAuth2User 객체 생성하여 반환
	 */
	@Override
	@Transactional
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
		OAuth2User oAuth2User = delegate.loadUser(userRequest);

		// 현재 진행중인 서비스를 구분하기 위해 문자열로 받음
		String registrationId = userRequest.getClientRegistration().getRegistrationId();
		log.info("OAuth2 Provider: {}", registrationId);

		// OAuth2 로그인 시 키가 되는 필드값 (PK)
		String usernameAttributeName = userRequest.getClientRegistration()
				.getProviderDetails()
				.getUserInfoEndpoint()
				.getUserNameAttributeName();
		log.info("Username attribute name: {}", usernameAttributeName);

		// OAuth2UserInfo 객체 생성
		OAuth2UserInfo userInfo = switch (registrationId.toLowerCase()) {
			case "kakao" -> new KakaoOAuth2UserInfo(oAuth2User.getAttributes());
			case "google" -> new GoogleOAuth2UserInfo(oAuth2User.getAttributes());
			case "github" -> new GithubOAuth2UserInfo(oAuth2User.getAttributes());
			default -> throw new OAuth2AuthenticationException("지원하지 않는 소셜 로그인입니다: " + registrationId);
		};

		log.info("Extracted user info - id:{}, email: {}, name: {}",
				userInfo.getId(), userInfo.getEmail(), userInfo.getName());

		// 유저 정보 저장 또는 업데이트
		Member member = saveOrUpdateWithLinking(userInfo, registrationId);
		log.info("Saved/Updated member - id: {}, email: {}",
				member.getId(), member.getEmail());

		return new CustomOAuth2User(
				Collections.singleton(new SimpleGrantedAuthority(member.getRole().name())),
				oAuth2User.getAttributes(),
				usernameAttributeName,
				member.getId(),
				member.getEmail());
	}

	/**
	 * OAuth2 사용자 정보로 회원가입 또는 정보 업데이트
	 *
	 * @param userInfo       OAuth2 제공자로부터 받은 사용자 정보
	 * @param registrationId OAuth2 서비스 구분자 (kakao, google 등)
	 * @return 저장 또는 업데이트된 회원 엔티티
	 */
	private Member saveOrUpdateWithLinking(OAuth2UserInfo userInfo, String registrationId) {
		Provider provider = getProvider(registrationId);

		// 1. 소셜 계정이 이미 있는지 확인 (Provider + ProviderId로)
		Optional<Member> optionalMember = memberRepository
				.findByProviderAndProviderId(provider, userInfo.getId());

		if (optionalMember.isPresent()) {
			// 기존 소셜 계정이 있으면 정보 업데이트하고 해당 계정으로 로그인
			Member member = optionalMember.get();
			member.updateOAuth2Profile(
					userInfo.getName(),
					userInfo.getImageUrl());
			log.info("기존 소셜 계정으로 로그인: {}", member.getEmail());
			return member;
		} else {
			// 2. 새 소셜 계정 생성 및 반환 (일반 계정 연동 여부 무시)
			Member member = Member.createSocialMember(userInfo, provider);
			log.info("신규 소셜 계정 생성: {}, provider: {}", userInfo.getEmail(), provider);
			return memberRepository.save(member);
		}
	}

	/**
	 * registrationId를 Provider enum으로 변환
	 * 
	 * @param registrationId OAuth2 서비스 구분 ID (kakao, google 등)
	 * @throws OAuth2AuthenticationException 지원하지 않는 OAuth2 제공자일 경우
	 */
	private Provider getProvider(String registrationId) {
		try {
			return Provider.valueOf(registrationId.toUpperCase());
		} catch (IllegalArgumentException e) {
			log.warn("지원하지 않는 OAuth2 Provider 요청: {}", registrationId);
			throw new OAuth2AuthenticationException("지원하지 않는 소셜 로그인입니다: " + registrationId);
		}
	}
}

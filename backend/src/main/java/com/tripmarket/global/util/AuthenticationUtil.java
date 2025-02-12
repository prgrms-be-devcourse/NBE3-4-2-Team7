package com.tripmarket.global.util;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.tripmarket.domain.member.entity.Member;
import com.tripmarket.domain.member.repository.MemberRepository;
import com.tripmarket.global.oauth2.CustomOAuth2User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 인증 관련 유틸리티 클래스
 * 현재 인증된 사용자의 권한 확인 등의 기능 제공
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationUtil {

	private final MemberRepository memberRepository;

	/**
	 * 사용자가 가이드 권한을 가지고 있는지 확인
	 * Role에 ROLE_GUIDE를 추가하지 않고 hasGuideProfile 필드로 가이드 여부를 판단
	 * Role이 추가되거나 자체 로그인 구현 시 수정해야됨
	 *
	 * @param authentication 현재 인증 정보
	 * @return 가이드 프로필 여부
	 */
	public boolean isGuide(Authentication authentication) {
		if (authentication == null || !authentication.isAuthenticated()) {
			log.debug("Authentication is null or authenticated");
			return false;
		}

		return Optional.ofNullable(authentication.getPrincipal())
			.filter(principal -> principal instanceof CustomOAuth2User) // OAuth2 기반 User인지 체크
			.map(principal -> (CustomOAuth2User)principal)
			.map(CustomOAuth2User::getId)
			.flatMap(memberRepository::findById)
			.map(Member::getHasGuideProfile)
			.orElseGet(() -> {
				log.debug("Failed to verify guide profile");
				return false;
			});
	}
}

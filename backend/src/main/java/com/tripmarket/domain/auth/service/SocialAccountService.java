package com.tripmarket.domain.auth.service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tripmarket.domain.auth.dto.SocialLinkRequest;
import com.tripmarket.domain.auth.dto.SocialLinkResponse;
import com.tripmarket.domain.auth.dto.SocialLinkResponse.LinkedSocialAccount;
import com.tripmarket.domain.member.entity.Member;
import com.tripmarket.domain.member.entity.Provider;
import com.tripmarket.domain.member.entity.SocialAccountLink;
import com.tripmarket.domain.member.repository.MemberRepository;
import com.tripmarket.domain.member.repository.SocialAccountLinkRepository;
import com.tripmarket.global.exception.CustomException;
import com.tripmarket.global.exception.ErrorCode;
import com.tripmarket.global.oauth2.service.OAuth2UserInfoService;
import com.tripmarket.global.oauth2.userinfo.OAuth2UserInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SocialAccountService {
	private final MemberRepository memberRepository;
	private final SocialAccountLinkRepository socialAccountLinkRepository;
	private final OAuth2UserInfoService oAuth2UserInfoService;

	/**
	 * 소셜 계정 연동
	 */
	@Transactional
	public void linkSocialAccount(Long memberId, SocialLinkRequest request) {
		// 회원 조회
		Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

		// 이미 연동된 계정인지 확인
		if (member.hasSocialLink(request.provider())) {
			throw new CustomException(ErrorCode.ALREADY_LINKED_ACCOUNT);
		}

		// 소셜 사용자 정보 조회
		OAuth2UserInfo userInfo = oAuth2UserInfoService.loadUserInfoByToken(request.provider(), request.token());

		// 연동 정보 저장
		SocialAccountLink socialLink = SocialAccountLink.builder()
				.member(member)
				.provider(request.provider())
				.providerId(userInfo.getId())
				.email(userInfo.getEmail())
				.name(userInfo.getName())
				.profileImageUrl(userInfo.getImageUrl())
				.build();

		member.addSocialLink(socialLink);
		memberRepository.save(member);

		log.info("계정 연동 완료 - 회원ID: {}, 제공자: {}", memberId, request.provider());
	}

	/**
	 * 소셜 계정 연동 해제
	 */
	@Transactional
	public void unlinkSocialAccount(Long memberId, Provider provider) {
		Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

		if (!member.hasSocialLink(provider)) {
			throw new CustomException(ErrorCode.SOCIAL_ACCOUNT_NOT_LINKED);
		}

		member.removeSocialLink(provider);
		memberRepository.save(member);

		log.info("계정 연동 해제 완료 - 회원ID: {}, 제공자: {}", memberId, provider);
	}

	/**
	 * 연동된 소셜 계정 목록 조회
	 */
	@Transactional(readOnly = true)
	public SocialLinkResponse getLinkedSocialAccounts(Long memberId) {
		List<SocialAccountLink> links = socialAccountLinkRepository.findByMemberId(memberId);

		List<LinkedSocialAccount> linkedAccounts = links.stream()
				.map(link -> new LinkedSocialAccount(
						link.getProvider(),
						link.getEmail(),
						link.getLinkedAt().format(DateTimeFormatter.ISO_DATE_TIME)))
				.collect(Collectors.toList());

		return new SocialLinkResponse(linkedAccounts);
	}
}

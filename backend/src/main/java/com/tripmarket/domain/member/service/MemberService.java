package com.tripmarket.domain.member.service;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tripmarket.domain.match.dto.GuideRequestDto;
import com.tripmarket.domain.match.dto.TravelOfferDto;
import com.tripmarket.domain.match.repository.GuideRequestRepository;
import com.tripmarket.domain.match.repository.TravelOfferRepository;
import com.tripmarket.domain.member.dto.MemberResponseDTO;
import com.tripmarket.domain.member.entity.Member;
import com.tripmarket.domain.member.repository.MemberRepository;
import com.tripmarket.domain.travel.dto.TravelDto;
import com.tripmarket.domain.travel.entity.Travel;
import com.tripmarket.domain.travel.repository.TravelRepository;
import com.tripmarket.global.exception.CustomException;
import com.tripmarket.global.exception.ErrorCode;
import com.tripmarket.global.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;
	private final GuideRequestRepository guideRequestRepository;
	private final TravelRepository travelRepository;
	private final TravelOfferRepository travelOfferRepository;

	@Transactional(readOnly = true)
	public MemberResponseDTO getMyInfo(String email) {
		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new ResourceNotFoundException("해당 이메일의 회원을 찾을 수 없습니다."));

		return MemberResponseDTO.from(member);
	}

	public Member getMember(Long userId) {
		return memberRepository.findById(userId)
			.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
	}

	public Member getMemberByEmail(String email) {
		return memberRepository.findByEmail(email)
			.orElseThrow(() -> new ResourceNotFoundException("해당 이메일의 회원을 찾을 수 없습니다."));
	}

	public List<GuideRequestDto> getGuideRequestsByRequester(String email) {
		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new ResourceNotFoundException("해당 이메일의 회원을 찾을 수 없습니다."));

		return guideRequestRepository.findDetailedByMemberId(member.getId()).stream()
			.map(GuideRequestDto::of) // Entity -> DTO 변환
			.toList();
	}

	@Transactional(readOnly = true)
	public List<TravelDto> getMyTravels(String email) {
		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new ResourceNotFoundException("해당 이메일의 회원을 찾을 수 없습니다."));

		List<Travel> travels = travelRepository.findByUser(member);
		return travels.stream()
			.map(TravelDto::of) // Entity -> DTO 변환
			.toList();
	}

	public List<TravelOfferDto> getTravelOffersForUser(String email) {
		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new ResourceNotFoundException("해당 이메일의 회원을 찾을 수 없습니다."));
		List<Travel> travels = travelRepository.findByUser(member);

		if (travels.isEmpty()) {
			return Collections.emptyList();
		}

		List<Long> travelIds = travels.stream().map(Travel::getId).toList();
		return travelOfferRepository.findByTravelIdIn(travelIds).stream()
			.map(TravelOfferDto::of)
			.toList();
	}

	public List<GuideRequestDto> getGuideRequestsByGuide(String email) {
		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new ResourceNotFoundException("해당 이메일의 회원을 찾을 수 없습니다."));

		if (member.getGuide() == null) {
			return Collections.emptyList();
		}

		return guideRequestRepository.findByGuideId(member.getGuide().getId()).stream()
			.map(GuideRequestDto::of)
			.toList();
	}

	public List<TravelOfferDto> getTravelOffersByGuide(String email) {
		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new ResourceNotFoundException("해당 이메일의 회원을 찾을 수 없습니다."));

		if (member.getGuide() == null) {
			return Collections.emptyList();
		}

		return travelOfferRepository.findByGuideId(member.getGuide().getId()).stream()
			.map(TravelOfferDto::of)
			.toList();
	}
}

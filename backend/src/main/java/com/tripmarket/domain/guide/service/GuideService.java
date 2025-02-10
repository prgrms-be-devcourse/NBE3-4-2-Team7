package com.tripmarket.domain.guide.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tripmarket.domain.guide.dto.GuideCreateRequest;
import com.tripmarket.domain.guide.dto.GuideDto;
import com.tripmarket.domain.guide.entity.Guide;
import com.tripmarket.domain.guide.repository.GuideRepository;
import com.tripmarket.domain.member.entity.Member;
import com.tripmarket.domain.member.repository.MemberRepository;
import com.tripmarket.domain.review.entity.Review;
import com.tripmarket.global.exception.CustomException;
import com.tripmarket.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GuideService {
	private final GuideRepository guideRepository;
	private final MemberRepository memberRepository;

	/*
	 * 변경하면 타 패키지에서 의존성 오류 생기므로 일단 보류
	 * */
	public Guide getGuide(Long id) {
		return guideRepository.findById(id)
			.orElseThrow(() -> new CustomException(ErrorCode.GUIDE_NOT_FOUND));
	}

	public GuideDto getGuideDto(Long id) {
		Guide guide = guideRepository.findById(id)
			.orElseThrow(() -> new CustomException(ErrorCode.GUIDE_NOT_FOUND));
		return GuideDto.fromEntity(guide);
	}

	@Transactional
	public void create(GuideCreateRequest createRequest, String email) {
		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

		// 이미 가이드 프로필이 존재하는지 확인
		if (member.hasGuideProfile()) {
			throw new CustomException(ErrorCode.ALREADY_HAS_GUIDE_PROFILE);
		}

		Guide guide = GuideCreateRequest.toEntity(createRequest);

		// 가이드 수정
		guide.setMember(member);

		// 멤버 수정
		member.addGuideProfile(guide);

		guideRepository.save(guide);
		memberRepository.save(member);
	}

	/**
	 * 유저가 마이페이지에서 자신의 가이드 프로필 조회
	 * */
	public GuideDto getGuideByMember(Long userId) {
		Guide guide = guideRepository.findByMemberId(userId)
			.orElseThrow(() -> new CustomException(ErrorCode.GUIDE_PROFILE_NOT_FOUND));
		return GuideDto.fromEntity(guide);
	}

	@Transactional
	public void update(Long memberId, GuideDto guideDto) {
		Guide guide = memberRepository.findById(memberId)
			.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND))
			.getGuide();
		guide.updateGuide(guideDto);
		guideRepository.save(guide);
	}

	public List<GuideDto> getAllGuides() {
		return guideRepository.findAll().stream()
			.map(GuideDto::fromEntity)
			.toList();
	}

	public void delete(Long id) {
		// 가이드 가져와서 상태 업데이트
		Guide guide = getGuide(id);
		guide.setDeleted(true);
		guideRepository.save(guide);
	}

	public List<Review> getAllReviews(Long id) {
		Guide guide = GuideDto.toEntity(getGuideDto(id));
		// TODO : review repository 에서 리뷰 전체 가져오기 ( 패치 사이즈 몇?)
		return List.of();
	}

	public boolean validateMyGuide(Long memberId, Long guideId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

		return Optional.ofNullable(member.getGuide())
			.map(guide -> Objects.equals(guide.getId(), guideId))
			.orElse(false);
	}

}

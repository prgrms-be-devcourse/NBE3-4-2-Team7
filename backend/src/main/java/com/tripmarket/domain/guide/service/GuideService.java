package com.tripmarket.domain.guide.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	public void create(GuideDto guideDto) {
		Member member = memberRepository.findById(guideDto.getUserId())
			.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
		Guide guide = GuideDto.toEntity(guideDto);

		// TODO : 멤버 이름과 Guide 생성할때 이름이 다른 경우엔? (우선은 그냥 하기)

		// 가이드 수정
		guide.setMember(member);
		guideRepository.save(guide);

		// 멤버 수정
		member.setGuide(guide);
		member.setHasGuideProfile(true);
		memberRepository.save(member);
	}

	public Guide getGuideByMember(Long userId) {
		return guideRepository.findByMemberId(userId)
			.orElseThrow(() -> new CustomException(ErrorCode.GUIDE_PROFILE_NOT_FOUND));
	}

	@Transactional
	public void update(GuideDto guideDto) {
		Member member = memberRepository.findById(guideDto.getUserId())
			.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

		Guide guide = guideRepository.findById(member.getGuide().getId())
			.orElseThrow(() -> new IllegalArgumentException("가이드를 찾을 수 없습니다."));

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
		GuideDto guideDto = getGuideDto(id);
		guideDto.setDeleted(true);
		guideRepository.save(GuideDto.toEntity(guideDto));
	}

	public List<Review> getAllReviews(Long id) {
		Guide guide = GuideDto.toEntity(getGuideDto(id));
		// TODO : review repository 에서 리뷰 전체 가져오기 ( 패치 사이즈 몇?)
		return List.of();
	}
}

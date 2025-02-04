package com.tripmarket.domain.review.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tripmarket.domain.guide.entity.Guide;
import com.tripmarket.domain.guide.repository.GuideRepository;
import com.tripmarket.domain.member.entity.Member;
import com.tripmarket.domain.member.repository.MemberRepository;
import com.tripmarket.domain.review.dto.ReviewCreateRequestDto;
import com.tripmarket.domain.review.entity.Review;
import com.tripmarket.domain.review.repository.ReviewRepository;
import com.tripmarket.domain.travel.repository.TravelRepository;
import com.tripmarket.global.exception.CustomException;
import com.tripmarket.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

	private final ReviewRepository reviewRepository;
	private final MemberRepository memberRepository;
	private final GuideRepository guideRepository;
	private final TravelRepository travelRepository;

	@Transactional
	public void createReview(ReviewCreateRequestDto requestDto) {
		// 멤버 존재 여부 확인
		Member member = memberRepository.findById(requestDto.memberId())
			.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

		//가이드 존재 여부 확인
		Guide guide = guideRepository.findById(requestDto.guideId())
			.orElseThrow(() -> new CustomException(ErrorCode.GUIDE_NOT_FOUND));

		// 해당 멤버가 해당 가이드와 여행을 완료했는지 검증 (현재는 MATCHED 상태를 COMPLETE로 간주)
		boolean hasMatchedTrip = travelRepository.hasMatchedTravel(member, guide);

		if (!hasMatchedTrip) {
			throw new CustomException(ErrorCode.REVIEW_CREATION_NOT_ALLOWED);
		}

		// 리뷰 점수 유효성 검증
		if (requestDto.reviewScore() < 0.0 || requestDto.reviewScore() > 5.0) {
			throw new CustomException(ErrorCode.INVALID_REVIEW_SCORE);
		}

		// 리뷰 생성 및 저장
		Review review = new Review(guide, member, requestDto.comment(), requestDto.reviewScore());
		reviewRepository.save(review);
	}
}

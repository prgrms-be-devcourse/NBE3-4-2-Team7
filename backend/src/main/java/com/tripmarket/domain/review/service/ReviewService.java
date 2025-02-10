package com.tripmarket.domain.review.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tripmarket.domain.guide.repository.GuideRepository;
import com.tripmarket.domain.match.repository.GuideRequestRepository;
import com.tripmarket.domain.match.repository.TravelOfferRepository;
import com.tripmarket.domain.member.entity.Member;
import com.tripmarket.domain.member.repository.MemberRepository;
import com.tripmarket.domain.review.dto.ReviewCreateRequestDto;
import com.tripmarket.domain.review.dto.ReviewResponseDto;
import com.tripmarket.domain.review.dto.ReviewUpdateRequestDto;
import com.tripmarket.domain.review.entity.Review;
import com.tripmarket.domain.review.repository.ReviewRepository;
import com.tripmarket.domain.reviewstats.service.ReviewStatsService;
import com.tripmarket.domain.travel.entity.Travel;
import com.tripmarket.domain.travel.repository.TravelRepository;
import com.tripmarket.global.exception.CustomException;
import com.tripmarket.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

	private final ReviewRepository reviewRepository;
	private final GuideRepository guideRepository;
	private final MemberRepository memberRepository;
	private final TravelRepository travelRepository;
	private final GuideRequestRepository guideRequestRepository;
	private final TravelOfferRepository travelOfferRepository;
	private final ReviewStatsService reviewStatsService;

	@Transactional
	public void createReview(ReviewCreateRequestDto requestDto, String email) {
		log.debug("리뷰 생성 메서드 실행됨. member_email: {}, travelId: {}", email, requestDto.travelId());

		// 이메일을 기반으로 Member 객체 조회
		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

		//여행 존재 여부 확인
		Travel travel = travelRepository.findById(requestDto.travelId())
			.orElseThrow(() -> new CustomException(ErrorCode.TRAVEL_NOT_FOUND));

		log.debug("리뷰 생성 요청: member_email: {}, travelId: {}", email, requestDto.travelId());
		log.debug("여행 상태: {}", travel.getStatus());

		// 여행이 완료 상태인지 확인
		if (!travel.isCompleted()) {
			throw new CustomException(ErrorCode.REVIEW_CREATION_NOT_ALLOWED);
		}

		// 여행 ID를 기반으로 가이드 ID 찾기 (GuideRequest 또는 TravelOffer에서 조회)
		Optional<Long> optionalGuideId = Stream.concat(
			guideRequestRepository.findGuideIdByTravelId(travel.getId()).stream(),
			travelOfferRepository.findGuideIdByTravelId(travel.getId()).stream()
		).findFirst();

		if (optionalGuideId.isEmpty()) {
			throw new CustomException(ErrorCode.GUIDE_NOT_FOUND);
		}

		Long guideId = optionalGuideId.get();
		log.debug("가이드 조회 완료: guideId: {}", guideId);

		// 작성자가 해당 여행에 대한 리뷰를 이미 작성했는지 확인 (중복 방지)
		if (reviewRepository.existsReviewByMemberAndTravel(member.getId(), travel.getId())) {
			throw new CustomException(ErrorCode.REVIEW_ALREADY_EXISTS);
		}

		// 리뷰 생성 및 저장
		Review review = Review.builder()
			.member(member) // 이메일로 조회한 member 객체 사용
			.travel(travel)
			.comment(requestDto.comment())
			.reviewScore(requestDto.reviewScore())
			.guideId(guideId) // 가이드 ID 저장
			.build();

		reviewRepository.save(review);
		log.debug("리뷰 저장 완료 - reviewId: {}", review.getId());

		reviewStatsService.updateReviewStatsOnCreate(guideId, requestDto.reviewScore());
	}


	// 특정 여행의 리뷰 조회
	public List<ReviewResponseDto> getReviewsByTravel(Long travelId) {
		Travel travel = travelRepository.findById(travelId)
			.orElseThrow(() -> new CustomException(ErrorCode.TRAVEL_NOT_FOUND));

		return reviewRepository.findReviewsByTravel(travel)
			.stream()
			.map(ReviewResponseDto::fromEntity)
			.collect(Collectors.toList());
	}

	// 특정 가이드의 리뷰 조회
	@Transactional(readOnly = true)
	public List<ReviewResponseDto> getReviewsByGuide(Long guideId) {
		log.debug(" 특정 가이드 리뷰 조회 요청 - guideId: {}", guideId);

		// 가이드 존재 여부 확인
		guideRepository.findById(guideId)
			.orElseThrow(() -> new CustomException(ErrorCode.GUIDE_NOT_FOUND));

		// 가이드가 참여한 여행 ID 조회 (List<Long> 사용)
		List<Long> travelIds = Stream.concat(
				guideRequestRepository.findTravelIdsByGuideId(guideId).stream(),
				travelOfferRepository.findTravelIdsByGuideId(guideId).stream()
			)
			.distinct() // 중복 제거
			.toList();  // 불필요한 Set 변환 제거

		log.debug(" 가이드가 참여한 여행 ID 조회 완료 - 여행 개수: {}", travelIds.size());

		if (travelIds.isEmpty()) {
			log.debug(" 해당 가이드가 참여한 여행 없음 - guideId: {}", guideId);
			return Collections.emptyList();
		}

		// 특정 여행 리스트에 해당하는 리뷰 조회
		List<Review> reviews = reviewRepository.findReviewsByTravelList(travelIds);


		return reviews.stream()
			.map(ReviewResponseDto::fromEntity)
			.toList();
	}

	// 리뷰 삭제
	@Transactional
	public void deleteReview(Long reviewId, String email) {
		// 리뷰 존재 여부 확인
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

		// 현재 로그인한 사용자 정보 가져오기
		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

		// 리뷰 작성자와 현재 로그인한 사용자가 같은지 확인
		if (!review.getMember().equals(member) && !member.isAdmin()) {
			throw new CustomException(ErrorCode.REVIEW_DELETION_FORBIDDEN);
		}

		// 삭제 전 리뷰 점수 저장
		double reviewScore = review.getReviewScore();
		Long guideId = review.getGuideId();

		// 소프트 삭제 처리
		review.softDelete();
		reviewRepository.save(review);

		// 리뷰 통계 업데이트
		reviewStatsService.updateReviewStats(guideId, reviewScore, null);
		log.debug("리뷰 삭제 완료 - reviewId: {}", review.getId());
	}

	@Transactional
	public void updateReview(Long reviewId, String email, ReviewUpdateRequestDto requestDto) {
		// 리뷰 존재 여부 확인
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

		// 삭제된 리뷰는 수정 불가
		if (review.isDeleted()) {
			throw new CustomException(ErrorCode.REVIEW_ALREADY_DELETED);
		}

		// 현재 로그인한 멤버
		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

		// 리뷰 작성자 이메일과 현재 로그인한 사용자 이메일 비교
		if (!review.getMember().equals(member) && !member.isAdmin()) {
			throw new CustomException(ErrorCode.REVIEW_UPDATE_FORBIDDEN);
		}

		// 수정 전 리뷰 점수 저장
		double oldScore = review.getReviewScore();

		// 리뷰 내용 & 평점 수정
		review.update(requestDto.comment(), requestDto.reviewScore());

		// 리뷰 통계 업데이트 (수정된 리뷰 반영)
		reviewStatsService.updateReviewStats(review.getGuideId(), oldScore, requestDto.reviewScore());
		log.debug("리뷰 수정 완료 - reviewId: {}, member_email: {}", reviewId, email);
	}
}

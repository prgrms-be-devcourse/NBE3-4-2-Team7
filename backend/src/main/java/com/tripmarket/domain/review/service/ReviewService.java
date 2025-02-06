package com.tripmarket.domain.review.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tripmarket.domain.guide.entity.Guide;
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

	@Transactional
	public void createReview(ReviewCreateRequestDto requestDto, Long memberId) {
		log.debug(" [DEBUG] 리뷰 생성 메서드 실행됨. memberId: {}, travelId: {}", memberId, requestDto.travelId());

		// 현재 로그인한 유저의 존재 여부 확인
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

		// 여행 존재 여부 확인
		Travel travel = travelRepository.findById(requestDto.travelId())
			.orElseThrow(() -> new CustomException(ErrorCode.TRAVEL_NOT_FOUND));

		log.info("리뷰 생성 요청: memberId: {}, travelId: {}", memberId, requestDto.travelId());
		log.info("여행 상태: {}", travel.getStatus());

		// 여행이 완료 상태인지 확인
		if (!travel.isCompleted()) {
			throw new CustomException(ErrorCode.REVIEW_CREATION_NOT_ALLOWED);
		}

		// 여행 ID를 기반으로 가이드 ID 찾기 (GuideRequest 또는 TravelOffer에서 조회)
		Long guideId = Stream.concat(
				guideRequestRepository.findGuideIdByTravelId(travel.getId()).stream(),
				travelOfferRepository.findGuideIdByTravelId(travel.getId()).stream()
			)
			.findFirst()
			.orElseThrow(() -> new CustomException(ErrorCode.GUIDE_NOT_FOUND));

		log.info("가이드 조회 완료: guideId: {}", guideId);

		// 특정 사용자가 해당 여행에 대한 리뷰를 이미 작성했는지 확인 (중복 방지)
		if (reviewRepository.existsByMemberAndTravelAndIsDeletedFalse(member, travel)) {
			throw new CustomException(ErrorCode.REVIEW_ALREADY_EXISTS);
		}

		// 리뷰 생성 및 저장
		Review review = Review.builder()
			.member(member)
			.travel(travel)
			.comment(requestDto.comment())
			.reviewScore(requestDto.reviewScore())
			.guideId(guideId) // 가이드 ID 저장
			.build();

		reviewRepository.save(review);
		log.info("리뷰 저장 완료 - reviewId: {}", review.getId());
	}

	// 이하 리뷰 조회
	// 특정 여행의 리뷰 조회 (리뷰가 없을 경우 빈 리스트 반환)
	public List<Review> getReviewsByTravel(Long travelId) {
		Travel travel = travelRepository.findById(travelId)
			.orElseThrow(() -> new CustomException(ErrorCode.TRAVEL_NOT_FOUND));

		return reviewRepository.findByTravelAndIsDeletedFalse(travel);
	}

	@Transactional(readOnly = true)
	public List<ReviewResponseDto> getReviewsByGuide(Long guideId) {
		log.info(" 특정 가이드 리뷰 조회 요청 - guideId: {}", guideId);

		// 가이드 존재 여부 확인
		Guide guide = guideRepository.findById(guideId)
			.orElseThrow(() -> new CustomException(ErrorCode.GUIDE_NOT_FOUND));

		log.info(" 가이드 확인 완료 - guideId: {}", guideId);

		// GuideRequest & TravelOffer에서 해당 가이드가 참여한 여행 ID 조회
		Set<Long> uniqueTravelIds = Stream.concat(
				guideRequestRepository.findTravelIdsByGuideId(guideId).stream(),
				travelOfferRepository.findTravelIdsByGuideId(guideId).stream()
			)
			.collect(Collectors.toSet());

		log.info(" 가이드가 참여한 여행 ID 조회 완료 - 여행 개수: {}", uniqueTravelIds.size());

		// 해당 가이드가 참여한 여행이 없다면 빈 리스트 반환
		if (uniqueTravelIds.isEmpty()) {
			log.info(" 해당 가이드가 참여한 여행 없음 - guideId: {}", guideId);
			return Collections.emptyList();
		}

		// 여행 객체 리스트 조회
		List<Travel> travelList = travelRepository.findAllById(uniqueTravelIds);
		log.info(" 가이드가 참여한 여행 리스트 조회 완료 - 개수: {}", travelList.size());

		if (travelList.isEmpty()) {
			log.info(" 해당 가이드가 참여한 여행 없음 - guideId: {}", guideId);
			return Collections.emptyList();
		}

		// 해당 여행의 리뷰 조회 (isDeleted=false 필터링 포함)
		List<Review> reviews = Optional.ofNullable(reviewRepository.findByTravelInAndIsDeletedFalse(travelList))
			.orElse(Collections.emptyList());

		List<ReviewResponseDto> reviewDtos = reviews.stream()
			.filter(review -> !review.isDeleted()) // 삭제된 리뷰 제외
			.map(ReviewResponseDto::fromEntity)
			.collect(Collectors.toList());

		log.info(" 특정 가이드 리뷰 조회 완료 - 리뷰 개수: {}", reviewDtos.size());

		return reviewDtos;
	}

	// 리뷰 삭제
	@Transactional
	public void deleteReview(Long reviewId, Long memberId) {
		// 리뷰 존재 여부 확인
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

		// 사용자 존재 여부 확인
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

		// 삭제 권한 검증 (리뷰 작성자 OR 관리자만 가능)
		if (!review.getMember().getId().equals(memberId) && !member.isAdmin()) {
			throw new CustomException(ErrorCode.REVIEW_DELETION_FORBIDDEN);
		}

		// 소프트 삭제 처리
		review.softDelete();

		// 삭제된 상태 저장
		reviewRepository.save(review);

		log.info("리뷰 삭제 완료 - reviewId: {}", review.getId());
	}

	@Transactional
	public void updateReview(Long reviewId, Long memberId, ReviewUpdateRequestDto requestDto) {
		// 리뷰 존재 여부 확인
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

		// 사용자 존재 여부 확인
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

		// 삭제된 리뷰는 수정 불가
		if (review.isDeleted()) {
			throw new CustomException(ErrorCode.REVIEW_ALREADY_DELETED);
		}

		// 수정 권한 검증 (리뷰 작성자 OR 관리자만 가능)
		if (!review.getMember().getId().equals(memberId) && !member.isAdmin()) {
			throw new CustomException(ErrorCode.REVIEW_UPDATE_FORBIDDEN);
		}

		// 리뷰 내용 & 평점 수정
		review.update(requestDto.comment(), requestDto.reviewScore());

		log.info("리뷰 수정 완료 - reviewId: {}, memberId: {}", reviewId, memberId);
	}

}

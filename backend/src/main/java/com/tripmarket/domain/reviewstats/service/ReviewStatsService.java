package com.tripmarket.domain.reviewstats.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tripmarket.domain.reviewstats.dto.ReviewStatsResponseDto;
import com.tripmarket.domain.reviewstats.entity.ReviewStats;
import com.tripmarket.domain.reviewstats.repository.ReviewStatsRepository;
import com.tripmarket.global.exception.CustomException;
import com.tripmarket.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewStatsService {
	private final ReviewStatsRepository reviewStatsRepository;

	// 새로운 리뷰가 추가될 때 통계를 업데이트
	@Transactional
	public void updateReviewStatsOnCreate(Long guideId, double reviewScore) {
		ReviewStats stats = reviewStatsRepository.findById(guideId)
			.orElseGet(() -> new ReviewStats(guideId, 0L, 0.0)); // 없으면 새로 생성

		stats.addReview(reviewScore); // 통계 업데이트
		reviewStatsRepository.save(stats);
	}

	// 리뷰 수정 및 삭제 시
	@Transactional
	public void updateReviewStats(Long guideId, double oldScore, Double newScore) {
		ReviewStats stats = reviewStatsRepository.findById(guideId)
			.orElseThrow(() -> new CustomException(ErrorCode.REVIEW_STATS_NOT_FOUND));

		if (newScore == null) {
			// 리뷰 삭제의 경우 (newScore가 null이면 삭제)
			stats.removeReview(oldScore);
		} else {
			// 리뷰 수정의 경우
			stats.updateReviewScore(oldScore, newScore);
		}

		reviewStatsRepository.save(stats);
	}

	// 특정 가이드의 리뷰 통계를 조회
	@Transactional(readOnly = true)
	public ReviewStatsResponseDto getReviewStats(Long guideId) {
		ReviewStats stats = reviewStatsRepository.findById(guideId)
			.orElseThrow(() -> new CustomException(ErrorCode.REVIEW_STATS_NOT_FOUND));

		return new ReviewStatsResponseDto(
			stats.getReviewCount(),
			stats.getAverageRating()
		);
	}
}

package com.tripmarket.domain.review.dto;

import java.time.LocalDateTime;

import com.tripmarket.domain.review.entity.Review;

import io.swagger.v3.oas.annotations.media.Schema;

public record ReviewResponseDto(
	@Schema(description = "리뷰 ID", example = "1")
	Long id,

	@Schema(description = "여행 ID", example = "100")
	Long travelId,

	@Schema(description = "가이드 ID", example = "5") // ✅ 가이드 ID 추가
	Long guideId,

	@Schema(description = "리뷰 작성자 ID", example = "3")
	Long memberId,

	@Schema(description = "리뷰 내용", example = "가이드가 친절하고 설명이 자세했어요!")
	String comment,

	@Schema(description = "리뷰 평점", example = "4.5")
	Double reviewScore,

	@Schema(description = "리뷰 작성 시간", example = "2025-02-03T16:30:00")
	LocalDateTime createdAt,

	@Schema(description = "리뷰 수정 시간", example = "2025-02-03T17:00:00")
	LocalDateTime updatedAt
) {
	public static ReviewResponseDto fromEntity(Review review) {
		return new ReviewResponseDto(
			review.getId(),
			review.getTravel().getId(),
			review.getGuideId(),
			review.getMember().getId(),
			review.getComment(),
			review.getReviewScore(),
			review.getCreatedAt(),
			review.getUpdatedAt()
		);
	}
}

package com.tripmarket.domain.review.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

public record ReviewResponseDto(
	@Schema(description = "리뷰 ID", example = "1")
	Long id,
	@Schema(description = "가이드 ID", example = "2")
	Long guideId,
	@Schema(description = "멤버 ID", example = "3")
	Long memberId,
	@Schema(description = "리뷰 내용", example = "가이드가 친절하고 설명이 자세했어요!")
	String comment,
	@Schema(description = "리뷰 평점", example = "4.5")
	double reviewScore,
	@Schema(description = "리뷰 작성 시간", example = "2025-02-03T16:30:00")
	LocalDateTime createdAt,
	@Schema(description = "리뷰 수정 시간", example = "2025-02-03T17:00:00")
	LocalDateTime updatedAt
) {
}

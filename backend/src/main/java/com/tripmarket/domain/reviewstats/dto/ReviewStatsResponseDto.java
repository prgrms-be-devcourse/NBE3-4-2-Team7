package com.tripmarket.domain.reviewstats.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "리뷰 통계 응답 DTO")
public record ReviewStatsResponseDto(
	@Schema(description = "총 리뷰 개수", example = "15")
	Long reviewCount,

	@Schema(description = "평균 별점", example = "4.5")
	Double averageRating
) {
}
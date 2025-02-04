package com.tripmarket.domain.review.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "리뷰 목록 응답 DTO")
public record ReviewListResponseDto(

	@ArraySchema(
		schema = @Schema(implementation = ReviewResponseDto.class, description = "리뷰 목록"),
		minItems = 0
	)
	List<ReviewResponseDto> reviews,

	@Schema(description = "전체 리뷰 개수", example = "50")
	long totalElements,

	@Schema(description = "전체 페이지 수", example = "5")
	int totalPages,

	@Schema(description = "현재 페이지 번호 (0부터 시작)", example = "1")
	int currentPage,

	@Schema(description = "첫 번째 페이지 여부", example = "false")
	boolean isFirst,

	@Schema(description = "마지막 페이지 여부", example = "true")
	boolean isLast
) {
}

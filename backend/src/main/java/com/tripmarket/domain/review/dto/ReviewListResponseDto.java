package com.tripmarket.domain.review.dto;

import java.util.List;

import com.tripmarket.domain.review.entity.Review;

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
	int currentPage, // 리액트와 협의하여 currentPage를 0부터 시작할지, 1부터 시작할지 결정해야 함

	@Schema(description = "이전 페이지 존재 여부", example = "false")
	boolean hasPrevious,  // 무한스크롤에서는 이전 페이지가 있는지 여부

	@Schema(description = "다음 페이지 존재 여부", example = "true")
	boolean hasNext  // 무한스크롤에서는 “더 보기” 버튼을 활성화할지를 결정
) {

	public static ReviewListResponseDto from(org.springframework.data.domain.Page<Review> reviewPage) {
		return new ReviewListResponseDto(
			reviewPage.getContent().stream().map(ReviewResponseDto::fromEntity).toList(),
			reviewPage.getTotalElements(),
			reviewPage.getTotalPages(),
			reviewPage.getNumber() + 1, // 0부터 시작하는 값을 1부터 시작하도록 변경
			reviewPage.hasPrevious(),
			reviewPage.hasNext()
		);
	}
}
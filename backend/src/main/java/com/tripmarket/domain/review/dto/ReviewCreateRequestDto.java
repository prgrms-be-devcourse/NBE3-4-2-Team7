package com.tripmarket.domain.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "리뷰 생성 요청 DTO")
public record ReviewCreateRequestDto(

	@NotNull(message = "여행 ID는 필수입니다.")
	@Schema(description = "리뷰를 남길 여행의 ID", example = "1")
	Long travelId,

	@NotBlank(message = "리뷰 내용은 필수입니다.")
	@Schema(description = "리뷰 내용", example = "정말 좋은 가이드였습니다!")
	String comment,

	@NotNull(message = "리뷰 점수는 필수입니다.")
	@DecimalMin(value = "0.0", message = "리뷰 점수는 최소 0.0점 이상이어야 합니다.")
	@DecimalMax(value = "5.0", message = "리뷰 점수는 최대 5.0점까지 가능합니다.")
	@Schema(description = "리뷰 점수 (0.0 ~ 5.0)", example = "4.5")
	Double reviewScore
) {
}

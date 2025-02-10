package com.tripmarket.domain.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "리뷰 수정 요청 DTO")
public record ReviewUpdateRequestDto(

	@Schema(description = "리뷰 내용", example = "가이드님이 정말 친절했어요!")
	@NotBlank(message = "리뷰 내용은 필수입니다.")
	String comment,

	@Schema(description = "리뷰 점수 (0.0 ~ 5.0)", example = "4.5")
	@NotNull(message = "리뷰 점수는 필수입니다.")
	@DecimalMin(value = "0.0", message = "최소 점수는 0.0입니다.")
	@DecimalMax(value = "5.0", message = "최대 점수는 5.0입니다.")
	Double reviewScore
) {
}

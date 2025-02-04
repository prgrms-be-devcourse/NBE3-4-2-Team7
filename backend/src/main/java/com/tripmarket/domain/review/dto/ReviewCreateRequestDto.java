package com.tripmarket.domain.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReviewCreateRequestDto(

	@NotNull(message = "가이드 ID는 필수입니다.")
	@Schema(description = "리뷰를 남길 가이드의 ID", example = "1")
	Long guideId,

	@NotNull(message = "멤버 ID는 필수입니다.")
	@Schema(description = "리뷰를 작성하는 회원의 ID", example = "2")
	Long memberId,

	@NotBlank(message = "리뷰 내용은 필수입니다.")
	@Schema(description = "리뷰 내용", example = "정말 좋은 가이드였습니다!")
	String comment,

	@NotNull(message = "리뷰 점수는 필수입니다.")
	@DecimalMin(value = "0.0", message = "리뷰 점수는 최소 0점 이상이어야 합니다.")
	@DecimalMax(value = "5.0", message = "리뷰 점수는 최대 5점까지 가능합니다.")
	@Schema(description = "리뷰 점수 (0.0 ~ 5.0)", example = "4.5")
	Double reviewScore
) {
}

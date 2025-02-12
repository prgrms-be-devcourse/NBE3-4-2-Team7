package com.tripmarket.domain.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record ReviewDeleteRequestDto(
	@NotNull(message = "리뷰 ID는 필수입니다.")
	@Schema(description = "삭제할 리뷰 ID", example = "1")
	Long reviewId,

	@NotNull(message = "사용자 ID는 필수입니다.")
	@Schema(description = "삭제 요청을 하는 사용자 ID", example = "4")
	Long memberId
) {
}

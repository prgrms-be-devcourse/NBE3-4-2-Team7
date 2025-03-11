package com.tripmarket.domain.auth.dto;

import com.tripmarket.domain.member.entity.Provider;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "소셜 계정 연동 요청")
public record SocialLinkRequest (
	@Schema(description = "소셜 로그인 제공자", example = "KAKAO")
	@NotNull(message = "소셜 로그인 제공자는 필수입니다.")
	Provider provider,

	@Schema(description = "소셜 인증 토큰")
	@NotBlank(message = "소셜 인증 토큰은 필수입니다.")
	String token
) {
}

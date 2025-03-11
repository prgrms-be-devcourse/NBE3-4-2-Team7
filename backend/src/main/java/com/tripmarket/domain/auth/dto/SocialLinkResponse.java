package com.tripmarket.domain.auth.dto;

import java.util.List;

import com.tripmarket.domain.member.entity.Provider;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "연동된 소셜 계정 목록 응답")
public record SocialLinkResponse(
	@Schema(description = "연동된 소셜 계정 목록")
	List<LinkedSocialAccount> linkedAccounts
) {
	@Schema(description = "연동된 소셜 계정 정보")
	public record LinkedSocialAccount(
		@Schema(description = "소셜 제공자")
		Provider provider,

		@Schema(description = "이메일")
		String email,

		@Schema(description = "연동 일시")
		String linkedAt
	) {	}
}

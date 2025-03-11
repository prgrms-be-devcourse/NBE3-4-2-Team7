package com.tripmarket.domain.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tripmarket.domain.auth.dto.SocialLinkRequest;
import com.tripmarket.domain.auth.dto.SocialLinkResponse;
import com.tripmarket.domain.auth.service.SocialAccountService;
import com.tripmarket.domain.member.entity.Provider;
import com.tripmarket.global.security.CustomUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/members/me")
@RequiredArgsConstructor
@Tag(name = "Social", description = "소셜 계정 연동 API")
public class SocialController {
	private final SocialAccountService socialAccountService;

	@PostMapping("/social-accounts")
	@Operation(summary = "소셜 계정 연동", description = "현재 로그인된 계정에 소셜 계정을 연동합니다.")
	@SecurityRequirement(name = "bearerAuth")
	public ResponseEntity<Void> linkSocialAccount(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody @Valid SocialLinkRequest request
	) {
		socialAccountService.linkSocialAccount(userDetails.getId(), request);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@GetMapping("/social-accounts")
	@Operation(summary = "연동된 소셜 계정 조회", description = "현재 계정에 연동된 소셜 계정 목록을 조회합니다.")
	@SecurityRequirement(name = "bearerAuth")
	public ResponseEntity<SocialLinkResponse> getLinkedSocialAccounts(
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		return ResponseEntity.status(HttpStatus.OK).body(socialAccountService.getLinkedSocialAccounts(userDetails.getId()));
	}

	@DeleteMapping("/social-accounts/{provider}")
	@Operation(summary = "소셜 계정 연동 해제", description = "특정 소셜 계정과의 연동을 해제합니다.")
	@SecurityRequirement(name = "bearerAuth")
	public ResponseEntity<Void> unlinkSocialAccount(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable Provider provider
	) {
		socialAccountService.unlinkSocialAccount(userDetails.getId(), provider);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
}

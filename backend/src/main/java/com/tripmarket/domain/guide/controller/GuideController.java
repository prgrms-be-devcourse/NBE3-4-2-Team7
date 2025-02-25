package com.tripmarket.domain.guide.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.tripmarket.domain.guide.dto.GuideCreateRequest;
import com.tripmarket.domain.guide.dto.GuideDto;
import com.tripmarket.domain.guide.dto.GuideProfileDto;
import com.tripmarket.domain.guide.service.GuideService;
import com.tripmarket.global.oauth2.CustomOAuth2User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestControllerAdvice
@RestController
@RequestMapping("/guides")
@Tag(name = "GuideController", description = "가이드 컨트롤러")
@RequiredArgsConstructor
public class GuideController {
	private final GuideService guideService;

	/**
	 * 가이드 리스트에서 조회하는 경우
	 * */
	@Operation(summary = "가이드 상세 조회")
	@GetMapping("/{id}")
	public ResponseEntity<GuideProfileDto> getGuideProfile(@PathVariable Long id) {
		GuideProfileDto guideProfileDto = guideService.getGuideProfile(id);
		return ResponseEntity.ok(guideProfileDto);
	}

	/**
	 * 마이페이지에서 조회하는 경우
	 * */
	@Operation(summary = "유저가 자신의 가이드 정보 조회할 때")
	@GetMapping("/me")
	public ResponseEntity<GuideProfileDto> getMyGuideProfile(@AuthenticationPrincipal CustomOAuth2User user) {
		GuideProfileDto guideProfileDto = guideService.getMyGuideProfile(user.getId());
		return ResponseEntity.ok(guideProfileDto);
	}

	@Operation(summary = "가이드 생성")
	@PostMapping
	public ResponseEntity<String> createGuide(
		@Valid @RequestBody GuideCreateRequest guideDto,
		@AuthenticationPrincipal CustomOAuth2User customOAuth2User
	) {
		guideService.create(guideDto, customOAuth2User.getEmail());
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@Operation(summary = "가이드 리스트 조회")
	@GetMapping
	public ResponseEntity<List<GuideDto>> getAllGuides() {
		List<GuideDto> guides = guideService.getAllGuides();
		return ResponseEntity.ok(guides);
	}

	@Operation(summary = "가이드 수정")
	@PatchMapping
	public ResponseEntity<String> updateGuide(
		@Valid @RequestBody GuideDto guideDto,
		@AuthenticationPrincipal CustomOAuth2User customOAuth2User
	) {
		guideService.update(customOAuth2User.getId(), guideDto);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "가이드 탈퇴")
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteGuide(@PathVariable Long id) {
		guideService.delete(id);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	/**
	 * 가이드 리스트에서 상세조회할때, 내 가이드 프로필인지 검사
	 * */
	@Operation(summary = "내 가이드 프로필인지 검사")
	@GetMapping("/{id}/verify")
	public ResponseEntity<Boolean> isMyGuideProfile(
		@PathVariable Long id,
		@AuthenticationPrincipal CustomOAuth2User customOAuth2User
	) {
		boolean isMyProfile = guideService.validateMyGuide(customOAuth2User.getId(), id);
		return ResponseEntity.ok(isMyProfile);
	}
}

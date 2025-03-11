package com.tripmarket.domain.guide.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import com.tripmarket.global.auth.AuthenticatedUser;
import com.tripmarket.global.oauth2.CustomOAuth2User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestControllerAdvice
@RestController
@RequestMapping("/guides")
@Tag(name = "GuideController", description = "가이드 컨트롤러")
public class GuideController {
	private final GuideService guideService;

	@Autowired
	public GuideController(GuideService guideService) {
		this.guideService = guideService;
	}

	/**
	 * 가이드 리스트에서 조회하는 경우
	 */
	@Operation(summary = "가이드 상세 조회")
	@GetMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public GuideProfileDto getGuideProfile(@PathVariable(name = "id") Long id) {
		return guideService.getGuideProfile(id);
	}

	// TODO : DTO 변경됨에 따라 해당 api 요청되는 프론트 코드도 수정 필요함.

	// public GuideDto getGuideById(@PathVariable(name="id") Long id) {
	// 	return guideService.getGuideDto(id);
	// }

	/**
	 * 마이페이지에서 조회하는 경우
	 */
	@Operation(summary = "유저가 자신의 가이드 정보 조회할 때")
	@GetMapping("/me")
	@ResponseStatus(HttpStatus.OK)
	public GuideProfileDto getMyGuideProfile(@AuthenticationPrincipal CustomOAuth2User user) {
		return guideService.getMyGuideProfile(user.getId());
	}
	// TODO : DTO 변경됨에 따라 해당 api 요청되는 프론트 코드도 수정 필요함.

	// public GuideDto getGuide(@AuthenticationPrincipal CustomOAuth2User user) {
	// 	return guideService.getGuideByMember(user.getId());
	// }

	@Operation(summary = "가이드 생성")
	@PostMapping
	public ResponseEntity<String> createGuide(
		@Valid @RequestBody GuideCreateRequest guideDto,
		@AuthenticationPrincipal AuthenticatedUser user
	) {
		guideService.create(guideDto, user.getEmail());
		return ResponseEntity.status(HttpStatus.CREATED).body("가이드가 성공적으로 생성되었습니다.");
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
		return ResponseEntity.ok("가이드 정보가 성공적으로 수정되었습니다.");
	}

	@Operation(summary = "가이드 탈퇴")
	@PatchMapping("/{id}")
	public ResponseEntity<String> deleteGuide(@PathVariable(name = "id") Long id) {
		guideService.delete(id);
		return ResponseEntity.ok("가이드 탈퇴가 완료되었습니다.");
	}

	@Operation(summary = "가이드 리뷰 전체 조회")
	@GetMapping("/{id}/reviews")
	public ResponseEntity<String> getAllReviews(@PathVariable(name = "id") Long id) {
		// TODO: 리뷰 dto 생성
		guideService.getAllReviews(id);
		return ResponseEntity.ok("가이드 리뷰 조회 기능이 준비 중입니다.");
	}

	/**
	 * 가이드 리스트에서 상세조회할때, 내 가이드 프로필인지 검사
	 */
	@Operation(summary = "내 가이드 프로필인지 검사")
	@GetMapping("/{id}/verify")
	public ResponseEntity<Boolean> isMyGuideProfile(
		@PathVariable(name = "id") Long id,
		@AuthenticationPrincipal CustomOAuth2User customOAuth2User
	) {
		boolean isMyProfile = guideService.validateMyGuide(customOAuth2User.getId(), id);
		return ResponseEntity.ok(isMyProfile);
	}
}

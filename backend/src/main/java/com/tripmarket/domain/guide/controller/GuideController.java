package com.tripmarket.domain.guide.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import com.tripmarket.domain.guide.service.GuideService;
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

	@Operation(summary = "가이드 상세 조회")
	@GetMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public GuideDto getGuideById(@PathVariable(name = "id") Long id) {
		return guideService.getGuideDto(id);
	}

	@Operation(summary = "가이드 생성")
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public void createGuide(@Valid @RequestBody GuideCreateRequest guideDto,
		@AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
		guideService.create(guideDto, customOAuth2User.getEmail());
	}

	@Operation(summary = "가이드 리스트 조회")
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public List<GuideDto> getAllGuides() {
		return guideService.getAllGuides();
	}

	@Operation(summary = "가이드 수정")
	@PatchMapping
	@ResponseStatus(HttpStatus.OK)
	public void updateGuide(@Valid @RequestBody GuideDto guideDto) {
		guideService.update(guideDto);
	}

	@Operation(summary = "가이드 탈퇴")
	@PatchMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public void deleteGuide(@PathVariable(name = "id") Long id) {
		guideService.delete(id);
	}

	@Operation(summary = "가이드 리뷰 전체 조회")
	@GetMapping("/{id}/reviews")
	@ResponseStatus(HttpStatus.OK)
	public void getAllReviews(@PathVariable(name = "id") Long id) {
		// TODO: 리뷰 dto 생성
		guideService.getAllReviews(id);
	}

}

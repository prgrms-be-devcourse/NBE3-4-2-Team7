package com.tripmarket.domain.travel.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tripmarket.domain.travel.dto.TravelDto;
import com.tripmarket.domain.travel.dto.request.TravelCreateRequest;
import com.tripmarket.domain.travel.dto.request.TravelUpdateRequest;
import com.tripmarket.domain.travel.service.TravelService;
import com.tripmarket.global.oauth2.CustomOAuth2User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/travels")
@RequiredArgsConstructor
@Tag(name = "Travel", description = "여행 요청 글 관련 API")
public class TravelController {

	private final TravelService travelService;

	@Operation(summary = "여행 요청 생성")
	@PostMapping()
	public ResponseEntity<TravelDto> createTravel(
		@AuthenticationPrincipal CustomOAuth2User oAuth2User,
		@RequestBody @Valid TravelCreateRequest requestDto) {

		TravelDto response = travelService.createTravel(oAuth2User.getEmail(), requestDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@Operation(summary = "여행 요청 수정")
	@PutMapping("/{travelId}")
	public ResponseEntity<TravelDto> updateTravel(
		@PathVariable Long travelId,
		@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
		@RequestBody @Valid TravelUpdateRequest requestDto) {
		TravelDto updatedTravel = travelService.updateTravel(travelId, customOAuth2User.getEmail(), requestDto);
		return ResponseEntity.ok(updatedTravel);
	}

	@Operation(summary = "여행 요청 전체 조회")
	@GetMapping
	public ResponseEntity<Page<TravelDto>> getTravels(
		@RequestParam(required = false) Long categoryId,
		@PageableDefault(size = 20) Pageable pageable) {
		Page<TravelDto> travelList = travelService.getTravels(categoryId, pageable);
		return ResponseEntity.ok(travelList);
	}

	@Operation(summary = "No OFFSET 방식으로 여행 요청 조회")
	@GetMapping("/no-offset")
	public ResponseEntity<List<TravelDto>> getTravelsNoOffset(
		@RequestParam(required = false) Long categoryId,
		@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime lastCreatedAt,
		@RequestParam(defaultValue = "20") int size) {
		List<TravelDto> travelList = travelService.getTravelsNoOffset(categoryId, lastCreatedAt, size);
		return ResponseEntity.ok(travelList);
	}

	@Operation(summary = "여행 요청 상세 조회")
	@GetMapping("/{travelId}")
	public ResponseEntity<TravelDto> getTravelDetail(
		@PathVariable Long travelId) {
		TravelDto travelDto = travelService.getTravelDetail(travelId);
		return ResponseEntity.ok(travelDto);
	}

	@Operation(summary = "여행 요청 삭제 (Soft Delete)")
	@PatchMapping("/{travelId}")
	public ResponseEntity<String> deleteTravel(
		@PathVariable Long travelId,
		@AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
		travelService.deleteTravel(travelId, customOAuth2User.getEmail());
		return ResponseEntity.ok("여행 요청 글이 삭제되었습니다.");
	}
}

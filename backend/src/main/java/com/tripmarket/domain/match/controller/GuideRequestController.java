package com.tripmarket.domain.match.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tripmarket.domain.match.dto.GuideRequestCreate;
import com.tripmarket.domain.match.entity.GuideRequest;
import com.tripmarket.domain.match.service.GuideRequestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/guide-requests")
@RequiredArgsConstructor
@Tag(name = "Match", description = "요청 매칭 관련 API")

public class GuideRequestController {

	private final GuideRequestService guideRequestService;

	@Operation(summary = "사용자가 가이더에게 여행 요청을 보냄")
	@PostMapping("/{guideId}")
	public ResponseEntity<String> createGuideRequest(
		@PathVariable Long guideId,
		@Parameter(description = "사용자 ID (임시, 추후 인증 객체로 변경 예정)") @RequestParam Long userId,
		@RequestBody @Valid GuideRequestCreate requestDto
	) {
		guideRequestService.createGuideRequest(userId, guideId, requestDto);
		return ResponseEntity.status(HttpStatus.CREATED).body("가이더에게 매칭 요청이 완료되었습니다.");
	}

	@Operation(summary = "가이더가 매칭 요청을 수락/거절",
		description = "사용자의 매칭 요청을 보고 가이더가 이를 수락하거나 거절하는 API입니다.")
	@PatchMapping("{requestId}/match")
	public ResponseEntity<String> matchGuideRequest(
		@PathVariable Long requestId,
		@RequestParam Long guideId,
		@RequestParam GuideRequest.RequestStatus status
	) {
		guideRequestService.matchGuideRequest(requestId, guideId, status);
		return ResponseEntity.ok("요청 상태가 업데이트되었습니다.");
	}
}

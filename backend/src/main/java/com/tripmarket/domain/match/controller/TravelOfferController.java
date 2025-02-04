package com.tripmarket.domain.match.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tripmarket.domain.match.entity.TravelOffer;
import com.tripmarket.domain.match.service.TravelOfferService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/travel-offers")
@RequiredArgsConstructor
@Tag(name = "Match", description = "여행 매칭 관련 API")
public class TravelOfferController {

	private final TravelOfferService travelOfferService;

	@Operation(summary = "가이더가 사용자의 여행 요청 글에 매칭 요청을 보냄")
	@PostMapping("/{travelId}/")
	public ResponseEntity<String> createTravelOffer(
		@PathVariable Long travelId,
		@Parameter(description = "가이더 ID (임시, 추후 인증 객체로 변경 예정)") @RequestParam Long userId
	) {
		travelOfferService.createTravelOffer(userId, travelId);
		return ResponseEntity.status(HttpStatus.CREATED).body("가이더 요청이 성공적으로 생성되었습니다.");
	}

	@Operation(summary = "사용자가 가이더의 매칭 요청을 수락/거절")
	@PatchMapping("/{requestId}/match")
	public ResponseEntity<String> matchTravelOffer(
		@PathVariable Long requestId,
		@Parameter(description = "사용자 ID (임시, 추후 인증 객체로 변경 예정)") @RequestParam Long userId,
		@RequestParam TravelOffer.RequestStatus status
	) {
		travelOfferService.matchTravelOffer(requestId, userId, status);
		return ResponseEntity.ok("가이더 요청 상태가 업데이트되었습니다.");
	}
}

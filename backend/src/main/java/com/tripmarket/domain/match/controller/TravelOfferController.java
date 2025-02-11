package com.tripmarket.domain.match.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tripmarket.domain.match.enums.MatchRequestStatus;
import com.tripmarket.domain.match.service.TravelOfferService;
import com.tripmarket.global.oauth2.CustomOAuth2User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/travel-offers")
@RequiredArgsConstructor
@Tag(name = "Match", description = "여행 매칭 관련 API")
public class TravelOfferController {

	private final TravelOfferService travelOfferService;

	@Operation(summary = "가이더가 사용자의 여행 요청 글에 매칭 요청을 보냄")
	@PostMapping("/{travelId}")
	public ResponseEntity<String> createTravelOffer(
		@PathVariable Long travelId,
		@AuthenticationPrincipal CustomOAuth2User customOAuth2User
	) {
		travelOfferService.createTravelOffer(customOAuth2User.getEmail(), travelId);
		return ResponseEntity.status(HttpStatus.CREATED).body("가이더 요청이 성공적으로 생성되었습니다.");
	}

	@Operation(summary = "사용자가 가이더의 매칭 요청을 수락/거절")
	@PatchMapping("/{requestId}/match")
	public ResponseEntity<String> matchTravelOffer(
		@PathVariable Long requestId,
		@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
		@RequestParam MatchRequestStatus status
	) {
		travelOfferService.matchTravelOffer(requestId, customOAuth2User.getEmail(), status);
		return ResponseEntity.ok("가이더 요청 상태가 업데이트되었습니다.");
	}

	/**
	 *	현재 로그인된 가이드의 ID와 여행 요청을 만든 유저 ID 값이 같은지 검사
	 * */
	@Operation(summary = "가이더가 자기 자신의 요청 글에 매칭을 보내는지 검사")
	@GetMapping("/{travelId}")
	public ResponseEntity<Boolean> validateSelfOffer(
		@PathVariable Long travelId,
		@AuthenticationPrincipal CustomOAuth2User customOAuth2User
	) {
		boolean isSelfOffer = travelOfferService.validateSelfOffer(customOAuth2User.getId(), travelId);
		return ResponseEntity.ok(isSelfOffer);
	}

	@Operation(summary = "사용자가 여행을 완료 상태로 변경",
		description = "사용자가 매칭된 여행 요청을 완료 상태로 변경할 수 있습니다.")
	@PatchMapping("{requestId}/complete")
	public ResponseEntity<String> completeTravelOffer(
		@PathVariable Long requestId,
		@AuthenticationPrincipal CustomOAuth2User customOAuth2User
	) {
		travelOfferService.completeTravelOffer(requestId, customOAuth2User.getEmail());
		return ResponseEntity.ok("여행이 완료 상태로 변경되었습니다.");
	}

}

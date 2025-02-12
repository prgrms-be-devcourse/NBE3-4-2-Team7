package com.tripmarket.domain.member.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tripmarket.domain.match.dto.GuideRequestDto;
import com.tripmarket.domain.match.dto.TravelOfferDto;
import com.tripmarket.domain.match.service.GuideRequestService;
import com.tripmarket.domain.member.dto.MemberResponseDTO;
import com.tripmarket.domain.member.entity.Member;
import com.tripmarket.domain.member.service.MemberService;
import com.tripmarket.domain.travel.dto.TravelDto;
import com.tripmarket.global.oauth2.CustomOAuth2User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
@Tag(name = "Members", description = "회원 관리 API")
public class MemberController {

	private final MemberService memberService;
	private final GuideRequestService guideRequestService;

	@GetMapping("/me")
	@Operation(summary = "내 정보 조회")
	public ResponseEntity<MemberResponseDTO> getMyInfo(@AuthenticationPrincipal CustomOAuth2User oAuth2User) {
		log.debug("OAuth2User: {}", oAuth2User);
		Member member = memberService.getMemberById(oAuth2User.getId());
		return ResponseEntity.ok(MemberResponseDTO.from(member));
	}

	/**
	 * 유저가 자신의 가이드 프로필이 존재하는지 검사
	 */
	@GetMapping("/me/guide")
	@Operation(summary = "내 가이드 프로필이 존재하는지 검사")
	public ResponseEntity<Boolean> hasGuideProfile(@AuthenticationPrincipal CustomOAuth2User oAuth2User) {
		boolean hasProfile = memberService.hasGuideProfile(oAuth2User.getId());
		return ResponseEntity.ok(hasProfile);
	}

	@GetMapping("/me/matchings/requester")
	@Operation(summary = "사용자 : 내가 요청한 가이드 요청 내역")
	public ResponseEntity<List<GuideRequestDto>> getGuideRequestsByRequester(
		@AuthenticationPrincipal CustomOAuth2User oAuth2User) {
		List<GuideRequestDto> guideRequests = memberService.getGuideRequestsByRequester(oAuth2User.getEmail());
		return ResponseEntity.ok(guideRequests);
	}

	@GetMapping("/me/matchings/travel-offers/received")
	@Operation(summary = "사용자 : 나의 여행 요청 글에 대한 가이더의 여행 제안 요청 내역")
	public ResponseEntity<List<TravelOfferDto>> getTravelOffersForMyTravel(
		@AuthenticationPrincipal CustomOAuth2User oAuth2User) {
		List<TravelOfferDto> travelOffers = memberService.getTravelOffersForUser(oAuth2User.getEmail());
		return ResponseEntity.ok(travelOffers);
	}

	@GetMapping("/me/travels")
	@Operation(summary = "사용자 : 나의 여행 요청 목록 조회")
	public ResponseEntity<List<TravelDto>> getMyTravels(
		@AuthenticationPrincipal CustomOAuth2User oAuth2User) {
		List<TravelDto> myTravels = memberService.getMyTravels(oAuth2User.getEmail());
		return ResponseEntity.ok(myTravels);
	}

	@GetMapping("/me/matchings/guide")
	@Operation(summary = "가이드 : 사용자가 나에게 요청한 가이드 요청 내역\t")
	public ResponseEntity<List<GuideRequestDto>> getGuideRequestsByGuide(
		@AuthenticationPrincipal CustomOAuth2User oAuth2User) {
		List<GuideRequestDto> guideRequestDto = memberService.getGuideRequestsByGuide(oAuth2User.getEmail());
		return ResponseEntity.ok(guideRequestDto);
	}

	@GetMapping("/me/matchings/travel-offers")
	@Operation(summary = "가이드 : 내가 사용자에게 보낸 여행 제안 요청 내역")
	public ResponseEntity<List<TravelOfferDto>> getTravelOffersByGuide(
		@AuthenticationPrincipal CustomOAuth2User oAuth2User) {
		List<TravelOfferDto> travelOffers = memberService.getTravelOffersByGuide(oAuth2User.getEmail());
		return ResponseEntity.ok(travelOffers);
	}

}

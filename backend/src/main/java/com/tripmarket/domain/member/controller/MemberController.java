package com.tripmarket.domain.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tripmarket.domain.member.dto.MemberResponseDTO;
import com.tripmarket.domain.member.entity.Member;
import com.tripmarket.domain.member.service.MemberService;
import com.tripmarket.global.oauth2.CustomOAuth2User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Members", description = "회원 관리 API")
public class MemberController {

	private final MemberService memberService;

	@GetMapping("/me")
	@Operation(summary = "내 정보 조회")
	public ResponseEntity<MemberResponseDTO> getMyInfo(@AuthenticationPrincipal CustomOAuth2User oAuth2User) {
		log.debug("OAuth2User: {}", oAuth2User);
		Member member = memberService.getMemberById(oAuth2User.getId());
		return ResponseEntity.ok(MemberResponseDTO.from(member));
	}
}

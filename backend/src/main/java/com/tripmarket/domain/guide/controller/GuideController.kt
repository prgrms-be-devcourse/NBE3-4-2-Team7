package com.tripmarket.domain.guide.controller;

import com.tripmarket.domain.guide.dto.GuideCreateRequest
import com.tripmarket.domain.guide.dto.GuideDto
import com.tripmarket.domain.guide.dto.GuideProfileDto
import com.tripmarket.domain.guide.service.GuideService
import com.tripmarket.global.oauth2.CustomOAuth2User
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import lombok.RequiredArgsConstructor
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/guides")
@Tag(name = "Guide", description = "가이드")
@RequiredArgsConstructor
class GuideController(
    private val guideService: GuideService
) {

    @Operation(summary = "가이드 상세 조회")
    @GetMapping("/{id}")
    fun getGuideProfile(@PathVariable id: Long): ResponseEntity<GuideProfileDto> {
        val guideProfileDto = guideService.getGuideProfile(id)
        return ResponseEntity.ok(guideProfileDto)
    }

    @Operation(summary = "유저가 자신의 가이드 정보 조회할 때")
    @GetMapping("/me")
    fun getMyGuideProfile(@AuthenticationPrincipal user: CustomOAuth2User): ResponseEntity<GuideProfileDto> {
        val guideProfileDto = guideService.getMyGuideProfile(user.id)
        return ResponseEntity.ok(guideProfileDto)
    }

    @Operation(summary = "가이드 생성")
    @PostMapping
    fun createGuide(
        @Valid @RequestBody guideDto: GuideCreateRequest,
        @AuthenticationPrincipal customOAuth2User: CustomOAuth2User
    ): ResponseEntity<Void> {
        guideService.create(guideDto, customOAuth2User.email)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @Operation(summary = "가이드 리스트 조회")
    @GetMapping
    fun getAllGuides(): ResponseEntity<List<GuideProfileDto>> {
        val guides = guideService.getAllGuides()
        return ResponseEntity.ok(guides)
    }

    @Operation(summary = "가이드 수정")
    @PatchMapping
    fun updateGuide(
        @Valid @RequestBody guideDto: GuideDto,
        @AuthenticationPrincipal customOAuth2User: CustomOAuth2User
    ): ResponseEntity<Void> {
        guideService.update(customOAuth2User.id, guideDto)
        return ResponseEntity.ok().build()
    }

    @Operation(summary = "가이드 탈퇴")
    @DeleteMapping("/{id}")
    fun deleteGuide(@PathVariable id: Long): ResponseEntity<Void> {
        guideService.delete(id)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }

    @Operation(summary = "내 가이드 프로필인지 검사")
    @GetMapping("/{id}/verify")
    fun isMyGuideProfile(
        @PathVariable id: Long,
        @AuthenticationPrincipal customOAuth2User: CustomOAuth2User
    ): ResponseEntity<Boolean> {
        val isMyProfile = guideService.validateMyGuide(customOAuth2User.id, id)
        return ResponseEntity.ok(isMyProfile)
    }
}
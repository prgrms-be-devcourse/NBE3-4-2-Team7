package com.tripmarket.domain.review.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tripmarket.domain.review.dto.ReviewCreateRequestDto;
import com.tripmarket.domain.review.dto.ReviewResponseDto;
import com.tripmarket.domain.review.dto.ReviewUpdateRequestDto;
import com.tripmarket.domain.review.service.ReviewService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

	private final ReviewService reviewService;

	@Operation(summary = "리뷰 생성", description = "완료된 여행에 대해 리뷰를 작성합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "리뷰 생성 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 또는 완료되지 않은 여행에 대한 리뷰 작성 불가"),
		@ApiResponse(responseCode = "404", description = "해당 여행 또는 사용자를 찾을 수 없음")
	})
	@PostMapping
	public ResponseEntity<Void> createReview(
		// @RequestBody @Valid ReviewCreateRequestDto requestDto,
		// @AuthenticationPrincipal UserDetails userDetails) {
		//
		// JWT에서 이메일(String) 가져오기
		// String email = userDetails.getUsername();
		//
		// 이메일을 기반으로 회원 정보 조회 (Long ID 가져오기)
		// Member member = memberRepository.findByEmailId(email)
		// 	.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
		//
		// 조회된 Member의 Long ID를 사용하여 리뷰 생성
		// reviewService.createReview(requestDto, member.getId());
		//
		// return ResponseEntity.status(HttpStatus.CREATED).build();
		@RequestBody @Valid
		@Parameter(description = "리뷰 생성 요청 DTO", required = true) ReviewCreateRequestDto requestDto) {

		Long memberId = 1L;

		reviewService.createReview(requestDto, memberId); // 여기서 직접 memberId 사용
		return ResponseEntity.status(HttpStatus.CREATED).build();

	}

	@Operation(summary = "특정 여행 리뷰 조회", description = "특정 여행에 대한 리뷰 목록을 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "조회 성공",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = ReviewResponseDto.class))),
		@ApiResponse(responseCode = "404", description = "해당 여행을 찾을 수 없음",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	@GetMapping("/travel/{travelId}")
	public ResponseEntity<List<ReviewResponseDto>> getReviewsByTravel(
		@PathVariable @Parameter(description = "조회할 여행의 ID", example = "1") Long travelId) {

		List<ReviewResponseDto> reviews = reviewService.getReviewsByTravel(travelId)
			.stream()
			.map(ReviewResponseDto::fromEntity)
			.collect(Collectors.toList());

		return ResponseEntity.ok(reviews);
	}

	@Operation(summary = "특정 가이드 리뷰 조회", description = "특정 가이드가 참여한 여행의 리뷰 목록을 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "조회 성공",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = ReviewResponseDto.class))),
		@ApiResponse(responseCode = "404", description = "가이드를 찾을 수 없음",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	@GetMapping("/guide/{guideId}")
	public ResponseEntity<List<ReviewResponseDto>> getReviewsByGuide(
		@PathVariable @Parameter(description = "조회할 가이드의 ID", example = "1") Long guideId) {

		List<ReviewResponseDto> reviews = reviewService.getReviewsByGuide(guideId);

		return ResponseEntity.ok(reviews);
	}

	@Operation(summary = "리뷰 삭제", description = "리뷰 작성자 또는 관리자가 특정 리뷰를 삭제합니다. (소프트 삭제)")
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "삭제 성공"),
		@ApiResponse(responseCode = "403", description = "삭제 권한 없음"),
		@ApiResponse(responseCode = "404", description = "리뷰 또는 사용자를 찾을 수 없음")
	})
	@DeleteMapping("/{reviewId}")
	public ResponseEntity<Void> deleteReview(
		@PathVariable @Parameter(description = "삭제할 리뷰의 ID", example = "1") Long reviewId,
		@RequestParam @Parameter(description = "요청한 사용자 ID", example = "4") Long memberId) {

		reviewService.deleteReview(reviewId, memberId);
		return ResponseEntity.noContent().build(); // 204 No Content 응답
	}

	@Operation(summary = "리뷰 수정", description = "리뷰 작성자 또는 관리자가 리뷰를 수정합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "리뷰 수정 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 또는 삭제된 리뷰 수정 불가"),
		@ApiResponse(responseCode = "403", description = "리뷰 수정 권한 없음"),
		@ApiResponse(responseCode = "404", description = "해당 리뷰 또는 사용자를 찾을 수 없음")
	})
	@PatchMapping("/{reviewId}")
	public ResponseEntity<Void> updateReview(
		@PathVariable @Parameter(description = "수정할 리뷰의 ID", example = "1") Long reviewId,
		@RequestBody @Valid ReviewUpdateRequestDto requestDto) {

		Long memberId = 1L; // JWT에서 사용자 ID 가져오는 로직 추가 필요
		reviewService.updateReview(reviewId, memberId, requestDto);
		return ResponseEntity.ok().build();
	}
}

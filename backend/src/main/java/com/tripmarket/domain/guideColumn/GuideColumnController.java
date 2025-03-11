package com.tripmarket.domain.guideColumn;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripmarket.domain.guideColumn.dto.GuideColumnRequestDTO;
import com.tripmarket.domain.guideColumn.dto.GuideColumnResponseDTO;
import com.tripmarket.domain.guideColumn.service.GuideColumnService;
import com.tripmarket.global.auth.AuthenticatedUser;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/guide-columns")
@RequiredArgsConstructor
@Tag(name = "GuideColumnController", description = "가이드 칼럼 컨트롤러")
public class GuideColumnController {
	private final GuideColumnService guideColumnService;

	@PostMapping
	@Operation(summary = "가이드 칼럼 작성")
	public ResponseEntity<GuideColumnResponseDTO> createColumn(
		@RequestPart(value = "data") String dataJson,  // Multipart 요청에서 JSON 파싱 과정에 문제가 발생하는거 같아서 임시 방편으로 String으로 받음
		@RequestPart(value = "images", required = false) List<MultipartFile> images,
		@AuthenticationPrincipal AuthenticatedUser user
	) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		GuideColumnRequestDTO requestDTO = mapper.readValue(dataJson, GuideColumnRequestDTO.class);

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(guideColumnService.createColumn(requestDTO, images, user));
	}

	@GetMapping
	@Operation(summary = "전체 가이드 칼럼 목록 조회")
	public ResponseEntity<Page<GuideColumnResponseDTO>> getColumns(
		@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
		Pageable pageable
	) {
		return ResponseEntity.ok(guideColumnService.getColumns(pageable));
	}

	@GetMapping("/guide/{guideId}")
	@Operation(summary = "특정 가이드의 칼럼 목록 조회")
	public ResponseEntity<List<GuideColumnResponseDTO>> getColumnsByGuide(
		@PathVariable Long guideId
	) {
		return ResponseEntity.ok(guideColumnService.getColumnsByGuide(guideId));
	}

	@PutMapping("/{columnId}")
	@Operation(summary = "가이드 칼럼 수정")
	public ResponseEntity<GuideColumnResponseDTO> updateColumn(
		@PathVariable Long columnId,
		@RequestPart(value = "data") String dataJson,
		@RequestPart(value = "images", required = false) List<MultipartFile> images,
		@AuthenticationPrincipal AuthenticatedUser user
	) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		GuideColumnRequestDTO requestDTO = mapper.readValue(dataJson, GuideColumnRequestDTO.class);
		return ResponseEntity
			.ok()
			.body(guideColumnService.updateColumn(columnId, requestDTO, images, user));
	}

	@DeleteMapping("/{columnId}")
	@Operation(summary = "가이드 칼럼 삭제")
	public ResponseEntity<Void> deleteColumn(
		@PathVariable Long columnId,
		@AuthenticationPrincipal AuthenticatedUser user
	) {
		guideColumnService.deleteColumn(columnId, user);
		return ResponseEntity.noContent().build();
	}
}
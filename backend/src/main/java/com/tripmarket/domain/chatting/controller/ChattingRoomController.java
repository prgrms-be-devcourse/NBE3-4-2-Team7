package com.tripmarket.domain.chatting.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tripmarket.domain.chatting.dto.ChattingRoomRequestDto;
import com.tripmarket.domain.chatting.service.ChattingRoomService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/chatting-room")
@Tag(name = "채팅룸", description = "채팅룸 컨트롤러")
@RequiredArgsConstructor
public class ChattingRoomController {

	private final ChattingRoomService chattingRoomService;

	@PostMapping
	@Operation(summary = "채팅방 생성", description = "채팅방을 생성하는 API")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "채팅방 생성"),
		@ApiResponse(responseCode = "404", description = "존재하지 않는 유저입니다."),
		@ApiResponse(responseCode = "409", description = "채팅방이 이미 존재"),
		@ApiResponse(responseCode = "500", description = "서버 오류")})
	public ResponseEntity<?> createChatRoom(@RequestBody @Valid ChattingRoomRequestDto request) {
		chattingRoomService.create(request);
		return ResponseEntity.ok(null);
	}
}

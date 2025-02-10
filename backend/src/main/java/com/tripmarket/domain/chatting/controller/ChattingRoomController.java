package com.tripmarket.domain.chatting.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tripmarket.domain.chatting.dto.ChattingResponseDto;
import com.tripmarket.domain.chatting.dto.ChattingRoomRequestDto;
import com.tripmarket.domain.chatting.dto.ChattingRoomsResponseDto;
import com.tripmarket.domain.chatting.dto.CreateChattingRoomResponseDto;
import com.tripmarket.domain.chatting.service.ChattingRoomService;
import com.tripmarket.global.oauth2.CustomOAuth2User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/chatting-room")
@Tag(name = "채팅룸", description = "채팅룸 컨트롤러")
@RequiredArgsConstructor
public class ChattingRoomController {

	private final ChattingRoomService chattingRoomService;

	@PostMapping
	@Operation(summary = "채팅방 생성", description = "채팅방을 생성하는 API")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "채팅방 생성"),
		@ApiResponse(responseCode = "404", description = "존재하지 않는 유저입니다."),
		@ApiResponse(responseCode = "500", description = "서버 오류")})
	public ResponseEntity<CreateChattingRoomResponseDto> createChatRoom(
		@RequestBody @Valid ChattingRoomRequestDto request,
		@AuthenticationPrincipal CustomOAuth2User user) {

		CreateChattingRoomResponseDto response = chattingRoomService.create(user.getEmail(),
			request.receiver());

		return ResponseEntity.ok().body(response);
	}

	@GetMapping("/my-list")
	@Operation(summary = "채팅방 목록", description = "채팅방을 목록을 확인하는 API")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "채팅방 목록 확인"),
		@ApiResponse(responseCode = "500", description = "서버 오류")})
	public ResponseEntity<Page<ChattingRoomsResponseDto>> getChattingRooms(
		@AuthenticationPrincipal CustomOAuth2User user,
		@RequestParam(value = "search", required = false) String search,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size) {

		String userEmail = user.getEmail();
		Pageable pageable = PageRequest.of(page, size);
		Page<ChattingRoomsResponseDto> result = chattingRoomService.findChattingRooms(userEmail, search, pageable);

		return ResponseEntity.ok(result);
	}

	@GetMapping("/{roomId}/messages")
	@Operation(summary = "채팅 내역", description = "채팅내역을 확인하는 API")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "채팅 내역 확인"),
		@ApiResponse(responseCode = "500", description = "서버 오류")})
	public ResponseEntity<Page<ChattingResponseDto>> getMessages(
		@PathVariable String roomId,
		@AuthenticationPrincipal CustomOAuth2User user,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size) {
		String userEmail = user.getEmail();
		chattingRoomService.markMessagesAsRead(roomId, userEmail);
		Page<ChattingResponseDto> messages = chattingRoomService.getChattingMessages(roomId,
			PageRequest.of(page, size));
		return ResponseEntity.ok(messages);
	}

	@PatchMapping("/{roomId}/leave")
	@Operation(summary = "채팅방 나가기", description = "사용자가 채팅방을 나가는 API")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "채팅 내역 확인"),
		@ApiResponse(responseCode = "404", description = "채팅방을 찾을 수 없음"),
		@ApiResponse(responseCode = "500", description = "서버 오류")})
	public ResponseEntity<Void> leaveChatRoom(
		@PathVariable String roomId,
		@AuthenticationPrincipal CustomOAuth2User user) {
		chattingRoomService.leaveChattingRoom(user.getEmail(), roomId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
	}
}

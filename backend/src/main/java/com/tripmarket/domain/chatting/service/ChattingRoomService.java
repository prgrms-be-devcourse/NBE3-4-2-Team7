package com.tripmarket.domain.chatting.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.tripmarket.domain.chatting.dto.ChattingResponseDto;
import com.tripmarket.domain.chatting.dto.ChattingRoomsResponseDto;
import com.tripmarket.domain.chatting.dto.CreateChattingRoomResponseDto;

public interface ChattingRoomService {
	CreateChattingRoomResponseDto create(String userEmail, String targetEmail);
	Page<ChattingRoomsResponseDto> findChattingRooms(String userEmail, String search, Pageable pageable);
	Page<ChattingResponseDto> getChattingMessages(String roomId, Pageable pageable);
	void leaveChattingRoom(String userEmail, String roomId);
	void markMessagesAsRead(String roomId, String userEmail);
}

package com.tripmarket.domain.chatting.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.tripmarket.domain.chatting.dto.ChattingResponseDto;
import com.tripmarket.domain.chatting.dto.ChattingRoomsResponseDto;
import com.tripmarket.domain.chatting.dto.CreateChattingRoomResponseDto;
import com.tripmarket.domain.chatting.dto.ReceiverResponseDto;

public interface ChattingRoomService {
	CreateChattingRoomResponseDto create(String userEmail, String targetEmail);
	Page<ChattingRoomsResponseDto> findChattingRooms(String userEmail, String search, Pageable pageable);
	List<ChattingResponseDto> getChattingMessages(String roomId);
	void leaveChattingRoom(String userEmail, String roomId);
	void markMessagesAsRead(String roomId, String userEmail);
	ReceiverResponseDto getReceiverEmail(String roomId, String userEmail);
}

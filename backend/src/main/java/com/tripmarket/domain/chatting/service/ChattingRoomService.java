package com.tripmarket.domain.chatting.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.tripmarket.domain.chatting.dto.ChattingRoomsResponseDto;

public interface ChattingRoomService {
	void create(String userEmail, String targetEmail);
	Page<ChattingRoomsResponseDto> findChattingRooms(String userEmail, String search, Pageable pageable);
}

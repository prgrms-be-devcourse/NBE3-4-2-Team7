package com.tripmarket.domain.chatting.repository.chattingroom;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.tripmarket.domain.chatting.entity.ChattingRoom;

public interface CustomChattingRoomRepository {
	boolean findRoomByParticipants(String email1, String email2);
	Page<ChattingRoom> findChattingRooms(String userEmail, String searchName, Pageable pageable);
	List<ChattingRoom> findChattingRoomsISDelete(LocalDateTime now);
}

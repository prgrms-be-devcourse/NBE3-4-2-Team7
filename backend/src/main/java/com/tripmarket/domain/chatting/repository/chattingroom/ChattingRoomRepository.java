package com.tripmarket.domain.chatting.repository.chattingroom;

import com.tripmarket.domain.chatting.entity.ChattingRoom;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ChattingRoomRepository extends JpaRepository<ChattingRoom, String>, CustomChattingRoomRepository {
}
package com.tripmarket.domain.chatting.repository.chattingroom;

public interface CustomChattingRoomRepository {
	boolean findRoomByParticipants(String email1, String email2);
}

package com.tripmarket.domain.chatting.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.tripmarket.domain.chatting.entity.ChattingRoom;
import com.tripmarket.domain.chatting.repository.chattingroom.ChattingRoomRepository;
import com.tripmarket.domain.chatting.repository.message.MessageRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChattingRoomScheduler {

	private final ChattingRoomRepository chattingRoomRepository;
	private final MessageRepository messageRepository;

	// 매일 자정 실행
	@Scheduled(cron = "0 0 0 * * ?")
	public void cleanupScheduledChatRooms() {
		List<ChattingRoom> chattingRooms = chattingRoomRepository.findChattingRoomsISDelete(LocalDateTime.now());

		for (ChattingRoom room : chattingRooms) {
			messageRepository.deleteByRoomId(room.getId());
			chattingRoomRepository.delete(room);
		}
	}
}

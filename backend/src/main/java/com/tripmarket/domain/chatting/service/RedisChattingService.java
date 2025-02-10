package com.tripmarket.domain.chatting.service;

import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisChattingService {

	private final RedisTemplate<String, Object> redisTemplate;

	// 채팅방에 유저 추가
	public void addUserToChatRoom(String roomId, String userEmail) {
		redisTemplate.opsForSet().add("chatRoom:" + roomId, userEmail);
	}

	// 채팅방에서 유저 제거
	public void removeUserFromChatRoom(String roomId, String userEmail) {
		redisTemplate.opsForSet().remove("chatRoom:" + roomId, userEmail);
	}

	// 채팅방에 접속 중인 유저 목록 가져오기
	public Set<Object> getUsersInChatRoom(String roomId) {
		return redisTemplate.opsForSet().members("chatRoom:" + roomId);
	}

	// 읽지 않은 메시지 수 증가
	public void incrementUnreadCount(String roomId, String receiver) {
		String key = "chatRoom:" + roomId + ":unread:" + receiver;
		redisTemplate.opsForValue().increment(key);
	}

	// 읽지 않은 메시지 수 가져오기
	public int getUnreadCount(String roomId, String receiver) {
		String key = "chatRoom:" + roomId + ":unread:" + receiver;
		String count = (String)redisTemplate.opsForValue().get(key);
		if (count == null || count.isEmpty()) {
			return 0;
		}

		return Integer.parseInt(count);
	}

	// 읽음 처리 후 Redis에서 초기화
	public void resetUnreadCount(String roomId, String receiver) {
		String key = "chatRoom:" + roomId + ":unread:" + receiver;
		redisTemplate.delete(key);
	}

	// 읽음 상태 전송
	public void sendReadReceipt(String receiver, String roomId, String messageId) {
		String receiptMessage = String.format("User [%s] read message [%s] in room [%s]", receiver, messageId, roomId);
		redisTemplate.convertAndSend("chat.read." + roomId, receiptMessage);
	}
}

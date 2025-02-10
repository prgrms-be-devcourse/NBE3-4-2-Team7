package com.tripmarket.global.handler;

import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;

import com.tripmarket.domain.chatting.service.RedisChattingService;
import com.tripmarket.global.exception.CustomException;
import com.tripmarket.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class StompHandler implements ChannelInterceptor {

	private final RedisChattingService redisChattingService;

	@Override
	public Message<?> preSend(@Nullable Message<?> message, @Nullable MessageChannel channel) {
		if (message == null) {
			return null;
		}

		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

		if (StompCommand.CONNECT.equals(accessor.getCommand())) {
			handleConnect(accessor);
		} else if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
			handleDisconnect(accessor);
		}

		return message;
	}

	private void handleConnect(StompHeaderAccessor accessor) {
		String roomId = accessor.getFirstNativeHeader("roomId");
		String userEmail = accessor.getFirstNativeHeader("userEmail");

		log.info("STOMP 연결 요청 - roomId: {}, userEmail: {}", roomId, userEmail);

		if (roomId == null || userEmail == null) {
			throw new CustomException(ErrorCode.STOMP_INVALID_HEADER);
		}

		redisChattingService.addUserChattingRoom(roomId, userEmail);
		redisChattingService.resetUnreadCount(roomId, userEmail);
	}

	private void handleDisconnect(StompHeaderAccessor accessor) {
		String roomId = accessor.getFirstNativeHeader("roomId");
		String userEmail = accessor.getFirstNativeHeader("userEmail");

		if (roomId == null || userEmail == null) {
			throw new CustomException(ErrorCode.STOMP_DISCONNECT_ERROR);
		}

		redisChattingService.deleteUserChattingRoom(roomId, userEmail);
	}
}

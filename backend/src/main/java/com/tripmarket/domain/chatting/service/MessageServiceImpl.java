package com.tripmarket.domain.chatting.service;

import java.util.Set;

import com.tripmarket.domain.chatting.dto.MessageDto;
import com.tripmarket.domain.chatting.entity.Message;
import com.tripmarket.domain.chatting.repository.message.MessageRepository;
import com.tripmarket.global.exception.CustomException;
import com.tripmarket.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

	private final MessageRepository messageRepository;
	private final RabbitTemplate rabbitTemplate;
	private final RedisChattingService redisChattingService;

	@Value("${spring.rabbitmq.chat.exchange.name}")
	private String chatExchangeName;

	@Transactional
	@Override
	public void sendMessage(MessageDto messageDto, String roomId) {

		try {
			rabbitTemplate.convertAndSend(chatExchangeName, "chat.room." + roomId, messageDto);
		} catch (Exception e) {
			throw new CustomException(ErrorCode.FAIL_MESSAGE_SEND);
		}

		Message message = messageDto.toMessageEntity();
		Set<Object> connectedUsers = redisChattingService.getUsersInChatRoom(roomId);
		boolean isReceiverConnected = connectedUsers.contains(messageDto.receiver());

		//메시지 생성 - 읽음 상태 바로 확인
		if (isReceiverConnected) {
			message.updateRead(true);
		} else {
			message.updateRead(false);
			redisChattingService.incrementUnreadCount(roomId, messageDto.receiver());
		}

		messageRepository.save(message);

		// 읽음 상태 실시간으로 반영할때
		if (isReceiverConnected) {
			redisChattingService.sendReadReceipt(messageDto.receiver(), roomId, message.getId());
		}
	}
}

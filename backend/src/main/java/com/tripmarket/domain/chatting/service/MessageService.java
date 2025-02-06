package com.tripmarket.domain.chatting.service;

import com.tripmarket.domain.chatting.dto.MessageDto;
import com.tripmarket.domain.chatting.entity.Message;
import com.tripmarket.domain.chatting.repository.message.MessageRepository;
import com.tripmarket.global.exception.MessageSendException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

	private final MessageRepository messageRepository;
	private final RabbitTemplate rabbitTemplate;

	@Value("${spring.rabbitmq.chat.exchange.name}")
	private String chatExchangeName;

	@Transactional
	public void sendMessageToRoom(MessageDto messageDto, String roomId) {
		try {
			rabbitTemplate.convertAndSend(chatExchangeName, "chat.room." + roomId, messageDto);
			log.info("메세지가 RabbitMQ로 전송되었습니다: {}", messageDto);
		} catch (MessageSendException e) {
			throw new MessageSendException("메세지 전송 중 오류 발생", e);
		}
		// DB에 메시지 저장
		Message message = messageDto.toMessageEntity();
		messageRepository.save(message);
		log.info("메세지가 DB에 저장되었습니다: {}", message.getId());
	}
}

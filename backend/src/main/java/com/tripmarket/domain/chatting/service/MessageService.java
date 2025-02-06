package com.tripmarket.domain.chatting.service;

import com.tripmarket.domain.chatting.dto.MessageDto;
import com.tripmarket.domain.chatting.entity.Message;
import com.tripmarket.domain.chatting.repository.MessageRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

	private final MessageRepository messageRepository;
	private final SimpMessagingTemplate messagingTemplate;

	@Transactional
	public void sendMessageToRoom(MessageDto messageDto) {
		// 대상 유저에게 실시간 메시지 전송
		String destination = "/queue/private." + messageDto.getToUserId();
		try {
			messagingTemplate.convertAndSend(destination, messageDto);
			log.info("메세지가 전송되었습니다 : {}, Destination: {}", messageDto.getToUserId(), destination);
		} catch (Exception e) {
			log.error("메세지 전송 실패 : {}", e.getMessage(), e);
		}

		Message message = messageDto.toMessageEntity();
		messageRepository.save(message);
		log.info("메세지가 저장되었습니다 : {}", message.getId());

	}
}

package com.tripmarket.domain.chatting.controller;

import com.tripmarket.domain.chatting.dto.MessageDto;
import com.tripmarket.domain.chatting.service.MessageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MessageController {

	private final MessageService messageService;

	@MessageMapping("chat.message.{roomId}")
	public void message(@DestinationVariable String roomId, MessageDto messageDto) {
		messageService.sendMessage(messageDto,roomId);
	}
}

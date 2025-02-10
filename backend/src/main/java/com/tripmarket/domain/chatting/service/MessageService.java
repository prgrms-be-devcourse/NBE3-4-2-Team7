package com.tripmarket.domain.chatting.service;

import com.tripmarket.domain.chatting.dto.MessageDto;

public interface MessageService {
	void sendMessage(MessageDto messageDto, String roomId);
}

package com.tripmarket.domain.chatting.dto;

import com.tripmarket.domain.chatting.entity.Message;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

@Builder
public record MessageDto(
	@NotEmpty
	String roomId,
	@NotEmpty
	String sender,
	@NotEmpty
	String receiver,
	@NotEmpty
	String content
) {
	public Message toMessageEntity() {
		return Message.builder()
			.roomId(roomId)
			.sender(sender)
			.receiver(receiver)
			.content(content)
			.readStatus(false)
			.build();
	}
}

package com.tripmarket.domain.chatting.dto;

import com.tripmarket.domain.chatting.entity.Message;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

@Builder
public record MessageDto(
	@NotEmpty
	String roomId,
	@NotEmpty
	String senderId,
	@NotEmpty
	String targetId,
	@NotEmpty
	String content
) {
	public Message toMessageEntity() {
		return Message.builder()
			.roomId(roomId)
			.senderId(senderId)
			.toUserId(targetId)
			.content(content)
			.build();
	}
}

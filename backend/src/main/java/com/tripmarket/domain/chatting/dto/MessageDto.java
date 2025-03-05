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
			.chattingRoomInfo(createChattingRoomInfo())
			.content(content)
			.build();
	}

	private Message.ChattingRoomInfo createChattingRoomInfo() {
		return Message.ChattingRoomInfo.builder()
			.roomId(roomId)
			.senderEmail(sender)
			.receiverEmail(receiver)
			.build();
	}
}

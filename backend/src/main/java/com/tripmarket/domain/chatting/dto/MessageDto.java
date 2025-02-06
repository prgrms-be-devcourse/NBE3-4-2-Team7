package com.tripmarket.domain.chatting.dto;

import com.tripmarket.domain.chatting.entity.Message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {
	private String roomId;
	private String sender;
	private String toUserId;
	private String content;

	public Message toMessageEntity(){
		return Message.builder()
			.roomId(roomId)
			.senderId(sender)
			.toUserId(toUserId)
			.content(content)
			.build();
	}
}

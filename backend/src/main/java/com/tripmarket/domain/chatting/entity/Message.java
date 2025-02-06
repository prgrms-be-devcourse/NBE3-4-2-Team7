package com.tripmarket.domain.chatting.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.tripmarket.global.jpa.entity.BaseEntity;

@Getter
@NoArgsConstructor
@Entity
public class Message extends BaseEntity {

	private String roomId;
	private String senderId;
	private String toUserId;
	private String content;

	@Builder
	public Message(String roomId, String senderId, String toUserId, String content) {
		this.roomId = roomId;
		this.senderId = senderId;
		this.toUserId = toUserId;
		this.content = content;
	}
}

package com.tripmarket.domain.chatting.entity;

import com.tripmarket.global.jpa.entity.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Message extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "고유 ID")
	private Long id;

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

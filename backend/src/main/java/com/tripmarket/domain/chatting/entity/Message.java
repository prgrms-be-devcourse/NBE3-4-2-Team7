package com.tripmarket.domain.chatting.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(collection = "message")
@CompoundIndexes({
	@CompoundIndex(name = "room_createdAt_idx", def = "{'roomId': 1, 'createdAt': -1}"),
	@CompoundIndex(name = "room_idx", def = "{'roomId': 1}"),
	@CompoundIndex(name = "room_receiver_readStatus_idx",
		def = "{'roomId': 1, 'receiver': 1, 'readStatus': 1}")
})
public class Message {

	@Id
	private String id;

	private String roomId;
	private String sender;
	private String receiver;
	private String content;
	private boolean readStatus;

	@CreatedDate
	@Column(name = "createdAt", updatable = false)
	private LocalDateTime createdAt;

	public void updateRead(boolean readStatus) {
		this.readStatus = readStatus;
	}
}
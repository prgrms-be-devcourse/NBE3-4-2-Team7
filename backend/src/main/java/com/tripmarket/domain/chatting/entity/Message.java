package com.tripmarket.domain.chatting.entity;

import java.time.LocalDateTime;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(collection = "messages")
@CompoundIndexes({
	@CompoundIndex(name = "room_idx", def = "{'chattingRoomInfo.roomId': 1}")
})
public class Message {

	@Id
	private ObjectId id;

	private ChattingRoomInfo chattingRoomInfo;
	private String content;

	@Builder
	public record ChattingRoomInfo(String roomId, String senderEmail, String receiverEmail) {}
}

package com.tripmarket.domain.chatting.dto;

import com.tripmarket.domain.chatting.entity.ChattingRoom;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ChattingRoomRequestDto {

	@NotNull
	private Long user1Id;

	@NotNull
	private Long user2Id;

	public ChattingRoom toChattingRoomEntity() {
		return ChattingRoom.builder()
			.user1Id(user1Id)
			.user2Id(user2Id)
			.build();
	}
}

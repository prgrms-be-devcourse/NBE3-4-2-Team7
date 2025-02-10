package com.tripmarket.domain.chatting.dto;

import lombok.Builder;

@Builder
public record CreateChattingRoomResponseDto(
	String roomId,
	String status
) {
	public static CreateChattingRoomResponseDto of(String roomId, String status) {
		return CreateChattingRoomResponseDto.builder()
			.roomId(roomId)
			.status(status)
			.build();
	}
}

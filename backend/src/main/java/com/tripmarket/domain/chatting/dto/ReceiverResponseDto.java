package com.tripmarket.domain.chatting.dto;

import lombok.Builder;

@Builder
public record ReceiverResponseDto(
	String receiver
) {
	public static ReceiverResponseDto of(String receiver) {
		return ReceiverResponseDto.builder()
			.receiver(receiver)
			.build();
	}
}


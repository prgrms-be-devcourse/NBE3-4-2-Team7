package com.tripmarket.domain.chatting.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

@Builder
public record ChattingRoomRequestDto(
	@NotEmpty
	@Email
	String receiver
) {
}


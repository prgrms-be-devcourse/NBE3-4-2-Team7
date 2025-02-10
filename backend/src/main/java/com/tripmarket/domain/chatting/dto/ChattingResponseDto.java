package com.tripmarket.domain.chatting.dto;

import com.tripmarket.domain.chatting.entity.Message;
import com.tripmarket.domain.member.entity.Member;
import com.tripmarket.global.util.DateUtil;

import lombok.Builder;

@Builder
public record ChattingResponseDto(
	String content,
	String senderName,
	String senderProfile,
	String time,
	boolean readStatus
) {
	public static ChattingResponseDto of(Message message, Member sender) {
		return ChattingResponseDto.builder()
			.content(message.getContent())
			.senderName(sender.getName())
			.senderProfile(sender.getImageUrl())
			.time(DateUtil.convertTime(message.getCreatedAt()))
			.readStatus(message.isReadStatus())
			.build();
	}
}

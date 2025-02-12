package com.tripmarket.domain.chatting.dto;

import com.tripmarket.domain.chatting.entity.Message;
import com.tripmarket.domain.member.entity.Member;
import com.tripmarket.global.util.DateUtil;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder
public record ChattingResponseDto(
	String content,
	String senderName,
	String senderEmail,
	String senderProfile,
	String time,
	boolean readStatus
) {
	public static ChattingResponseDto of(Message message, Member sender) {
		return ChattingResponseDto.builder()
			.content(message.getContent())
			.senderName(sender.getName())
			.senderEmail(message.getSender())
			.senderProfile(sender.getImageUrl())
			.time(DateUtil.convertTime(message.getCreatedAt()))
			.readStatus(message.isReadStatus())
			.build();
	}
}

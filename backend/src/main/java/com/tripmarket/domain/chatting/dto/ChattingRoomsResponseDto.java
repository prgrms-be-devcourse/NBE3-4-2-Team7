package com.tripmarket.domain.chatting.dto;

import com.tripmarket.domain.chatting.entity.Message;
import com.tripmarket.domain.member.entity.Member;
import com.tripmarket.global.util.DateUtil;

import lombok.Builder;

@Builder
public record ChattingRoomsResponseDto(
	String roomId,
	String name,
	String profileImage,
	String lastMessage,
	String lastMessageTime
) {
	public static ChattingRoomsResponseDto of(String roomId, Member target, Message message) {
		return ChattingRoomsResponseDto.builder()
			.roomId(roomId)
			.name(target.getName())
			.profileImage(target.getImageUrl())
			.lastMessage(message.getContent())
			.lastMessageTime(DateUtil.convertDateOrTime(DateUtil.getTime(message)))
			.build();
	}
}

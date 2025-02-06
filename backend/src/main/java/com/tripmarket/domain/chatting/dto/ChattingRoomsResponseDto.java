package com.tripmarket.domain.chatting.dto;

import com.tripmarket.domain.chatting.entity.Message;
import com.tripmarket.domain.member.entity.Member;
import com.tripmarket.global.util.DateUtil;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChattingRoomsResponseDto {
	private Long roomId;
	private String name;
	private String profileImage;
	private String lastMessage;
	private String lastMessageTime;

	public static ChattingRoomsResponseDto of (Long roomId, Member target, Message message){
		return ChattingRoomsResponseDto.builder()
			.roomId(roomId)
			.name(target.getName())
			.profileImage(target.getImageUrl())
			.lastMessage(message.getContent())
			.lastMessageTime(DateUtil.convertTime(message.getCreatedAt()))
			.build();
	}
}

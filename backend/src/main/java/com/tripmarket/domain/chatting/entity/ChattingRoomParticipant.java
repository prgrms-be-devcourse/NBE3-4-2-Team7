package com.tripmarket.domain.chatting.entity;

import com.tripmarket.domain.member.entity.Member;
import com.tripmarket.global.jpa.entity.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChattingRoomParticipant extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "room_id")
	private ChattingRoom chattingRoom;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	private boolean isActive;

	public void updateActive() {
		this.isActive = !this.isActive;
	}

	public static ChattingRoomParticipant create(ChattingRoom chattingRoom, Member member) {
		return ChattingRoomParticipant.builder()
			.chattingRoom(chattingRoom)
			.member(member)
			.isActive(true)
			.build();
	}

}

package com.tripmarket.domain.chatting.entity;

import com.tripmarket.domain.member.entity.Member;
import com.tripmarket.global.jpa.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class Chatting extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "chatting_room_id")
	private ChattingRoom chattingRoom;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private Member user;

	// 생성자
	public Chatting(ChattingRoom chattingRoom, Member user) {
		this.chattingRoom = chattingRoom;
		this.user = user;
	}

}

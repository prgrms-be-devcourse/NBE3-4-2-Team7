package com.tripmarket.domain.chatting.entity;

import com.tripmarket.global.jpa.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChattingRoom extends BaseEntity {

	@Column(nullable = false)
	private Long user1Id;  // 유저1

	@Column(nullable = false)
	private Long user2Id;  // 유저2
}

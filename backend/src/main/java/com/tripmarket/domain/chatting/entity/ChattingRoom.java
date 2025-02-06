package com.tripmarket.domain.chatting.entity;

import java.util.HashSet;
import java.util.Set;

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

	@OneToMany(mappedBy = "chattingRoom", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<ChattingRoomParticipant> participants = new HashSet<>();

	// 참여자 이메일
	public Set<String> getParticipantEmails() {
		Set<String> emails = new HashSet<>();
		for (ChattingRoomParticipant participant : participants) {
			emails.add(participant.getMember().getEmail());
		}
		return emails;
	}
}

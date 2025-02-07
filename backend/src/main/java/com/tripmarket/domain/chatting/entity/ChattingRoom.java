package com.tripmarket.domain.chatting.entity;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.UuidGenerator;

import com.tripmarket.global.jpa.entity.BaseTimeEntity;

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
public class ChattingRoom extends BaseTimeEntity {

	@Id
	@UuidGenerator
	@Column(length = 36, nullable = false, updatable = false)
	private String id;

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

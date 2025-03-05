package com.tripmarket.domain.chatting.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.UuidGenerator;

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
@Table(name = "chatting_room", indexes = {
	@Index(name = "idx_chattingRoom_isDelete", columnList = "isDelete")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChattingRoom extends BaseEntity {

	@Id
	@UuidGenerator
	@Column(length = 36, nullable = false, updatable = false)
	private String id;

	private boolean isDelete;
	private LocalDateTime deleteDate;

	@OneToMany(mappedBy = "chattingRoom", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<ChattingRoomParticipant> participants = new HashSet<>();

	public void deleteRoom() {
		this.isDelete = true;
		this.deleteDate = LocalDateTime.now().plusDays(7);
	}

	public static ChattingRoom create(){
		return ChattingRoom.builder()
			.participants(new HashSet<>())
			.isDelete(false)
			.deleteDate(null)
			.build();
	}

	// 참여자 이메일
	public Set<String> getParticipantEmails() {
		Set<String> emails = new HashSet<>();
		for (ChattingRoomParticipant participant : participants) {
			emails.add(participant.getMember().getEmail());
		}
		return emails;
	}
}

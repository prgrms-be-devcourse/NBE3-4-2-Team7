package com.tripmarket.domain.chatting.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tripmarket.domain.chatting.entity.ChattingRoom;
import com.tripmarket.domain.chatting.entity.ChattingRoomParticipant;
import com.tripmarket.domain.chatting.repository.chattingroom.ChattingRoomRepository;
import com.tripmarket.domain.member.entity.Member;
import com.tripmarket.domain.member.repository.MemberRepository;
import com.tripmarket.global.exception.CustomException;
import com.tripmarket.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChattingRoomServiceImpl implements ChattingRoomService {
	private final ChattingRoomRepository chattingRoomRepository;
	private final MemberRepository memberRepository;

	@Transactional
	public void create(String userEmail, String targetEmail) {
		Member user = validateMember(userEmail);
		Member target = validateMember(targetEmail);

		if (chattingRoomRepository.findRoomByParticipants(userEmail, targetEmail)) {
			throw new CustomException(ErrorCode.DUPLICATE_CHAT_ROOM);
		}

		ChattingRoom chattingRoom = ChattingRoom.builder()
			.participants(new HashSet<>())
			.build();
		addParticipants(chattingRoom, user, target);

		ChattingRoom save = chattingRoomRepository.save(chattingRoom);
		log.info("채팅방이 생성되었습니다. 채팅방 참여자: {}", String.join(", ", save.getParticipantEmails()));
	}

	private static void addParticipants(ChattingRoom chattingRoom, Member user, Member target) {
		Set<ChattingRoomParticipant> participants = new HashSet<>();
		participants.add(new ChattingRoomParticipant(chattingRoom, user));
		participants.add(new ChattingRoomParticipant(chattingRoom, target));
		chattingRoom.getParticipants().addAll(participants);
	}

	private Member validateMember(String email) {
		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
		log.info("유저를 찾았습니다. email ={}", member.getEmail());
		return member;
	}
}

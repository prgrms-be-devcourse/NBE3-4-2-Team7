package com.tripmarket.domain.chatting.service;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tripmarket.domain.chatting.dto.ChattingRoomsResponseDto;
import com.tripmarket.domain.chatting.entity.ChattingRoom;
import com.tripmarket.domain.chatting.entity.ChattingRoomParticipant;
import com.tripmarket.domain.chatting.entity.Message;
import com.tripmarket.domain.chatting.repository.chattingroom.ChattingRoomRepository;
import com.tripmarket.domain.chatting.repository.message.MessageRepository;
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
	private final MessageRepository messageRepository;

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

	@Override
	public Page<ChattingRoomsResponseDto> findChattingRooms(String userEmail, String search, Pageable pageable) {

		Page<ChattingRoom> chattingRoomsPage = chattingRoomRepository.findChattingRooms(userEmail, search, pageable);
		log.info("채팅방 목록을 받았습니다. 총 채팅방 수: {}", chattingRoomsPage.getTotalElements());

		List<String> roomIds = chattingRoomsPage.getContent().stream()
			.map(chattingRoom -> chattingRoom.getId().toString())
			.toList();
		log.info("채팅방 id들을 찾았습니다.");

		List<Message> latestMessages = messageRepository.findLatestMessages(roomIds);
		log.info("최신 메시지 조회 - 메시지 개수: {}", latestMessages.size());

		Map<String, Message> messageMap = latestMessages.stream()
			.collect((Collectors.toMap(Message::getRoomId, message -> message)));

		//채팅방 목록 받아오는 곳
		List<ChattingRoomsResponseDto> chattingRoomsResponse = chattingRoomsPage.getContent().stream()
			.map(chattingRoom -> {
				Long roomId = chattingRoom.getId();
				Message lastMessage = messageMap.get(roomId.toString());

				// 상대 정보 받아오기
				Member target = chattingRoom.getParticipants().stream()
					.filter(participant -> !participant.getMember().getEmail().equals(userEmail))
					.findFirst()
					.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND))
					.getMember();
				return ChattingRoomsResponseDto.of(roomId, target, lastMessage);
			})
			.sorted(Comparator.comparing(ChattingRoomsResponseDto::getLastMessageTime,
				Comparator.nullsFirst(Comparator.reverseOrder())))
			.toList();

		return new PageImpl<>(chattingRoomsResponse, pageable, chattingRoomsPage.getTotalElements());
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

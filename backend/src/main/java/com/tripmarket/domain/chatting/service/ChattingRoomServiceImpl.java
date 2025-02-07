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
	public void create(String userEmail, String receiverEmail) {
		Member user = findMember(userEmail);
		Member receiver = findMember(receiverEmail);
		validateChattingRoom(userEmail, receiverEmail);

		ChattingRoom chattingRoom = addParticipants(user, receiver);

		ChattingRoom save = chattingRoomRepository.save(chattingRoom);
		log.debug("채팅방이 생성되었습니다. 채팅방 참여자: {}", String.join(", ", save.getParticipantEmails()));
	}

	@Override
	public Page<ChattingRoomsResponseDto> findChattingRooms(String userEmail, String search, Pageable pageable) {
		Page<ChattingRoom> chattingRoomsPage = findChattingRoomsPage(userEmail, search, pageable);

		List<String> roomIds = findRoomIds(chattingRoomsPage);
		List<Message> latestMessages = messageRepository.findLatestMessages(roomIds);
		Map<String, Message> messageMap = getMessageMap(latestMessages);

		List<ChattingRoomsResponseDto> chattingRoomsResponse = getChattingRoomsResponse(userEmail, chattingRoomsPage, messageMap);

		return new PageImpl<>(chattingRoomsResponse, pageable, chattingRoomsPage.getTotalElements());
	}

	private static List<ChattingRoomsResponseDto> getChattingRoomsResponse(String userEmail, Page<ChattingRoom> chattingRoomsPage,
		Map<String, Message> messageMap) {
		return chattingRoomsPage.getContent().stream()
			.map(chattingRoom -> {
				Long roomId = chattingRoom.getId();
				Message lastMessage = messageMap.get(roomId.toString());
				Member receiver = getReceiver(userEmail, chattingRoom);
				return ChattingRoomsResponseDto.of(roomId, receiver, lastMessage);
			})
			.sorted(Comparator.comparing(ChattingRoomsResponseDto::lastMessageTime,
				Comparator.nullsFirst(Comparator.reverseOrder())))
			.toList();
	}

	private static Member getReceiver(String userEmail, ChattingRoom chattingRoom) {
		return chattingRoom.getParticipants().stream()
			.filter(participant -> !participant.getMember().getEmail().equals(userEmail))
			.findFirst()
			.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND))
			.getMember();
	}

	private static Map<String, Message> getMessageMap(List<Message> latestMessages) {
		return latestMessages.stream().collect(Collectors.toMap(Message::getRoomId, message -> message));
	}

	private static List<String> findRoomIds(Page<ChattingRoom> chattingRoomsPage) {
		return chattingRoomsPage.getContent().stream()
			.map(chattingRoom -> chattingRoom.getId().toString())
			.toList();
	}

	private Page<ChattingRoom> findChattingRoomsPage(String userEmail, String search, Pageable pageable) {
		return chattingRoomRepository.findChattingRooms(userEmail, search, pageable);
	}

	private ChattingRoom addParticipants(Member user, Member receiver) {
		ChattingRoom chattingRoom = ChattingRoom.builder()
			.participants(new HashSet<>())
			.build();
		Set<ChattingRoomParticipant> participants = new HashSet<>();
		participants.add(new ChattingRoomParticipant(chattingRoom, user));
		participants.add(new ChattingRoomParticipant(chattingRoom, receiver));
		chattingRoom.getParticipants().addAll(participants);
		return chattingRoom;
	}

	private void validateChattingRoom(String userEmail, String targetEmail) {
		if (chattingRoomRepository.findRoomByParticipants(userEmail, targetEmail)) {
			throw new CustomException(ErrorCode.DUPLICATE_CHAT_ROOM);
		}
	}

	private Member findMember(String email) {
		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
		log.debug("유저를 찾았습니다. email ={}", member.getEmail());
		return member;
	}
}

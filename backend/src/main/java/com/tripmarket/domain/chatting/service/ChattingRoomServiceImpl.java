package com.tripmarket.domain.chatting.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tripmarket.domain.chatting.dto.ChattingResponseDto;
import com.tripmarket.domain.chatting.dto.ChattingRoomsResponseDto;
import com.tripmarket.domain.chatting.dto.CreateChattingRoomResponseDto;
import com.tripmarket.domain.chatting.dto.ReceiverResponseDto;
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

	// 채팅방을 생성하며 각 상황에 맞게 status분리
	@Override
	@Transactional
	public CreateChattingRoomResponseDto create(String userEmail, String receiverEmail) {
		Member user = findMember(userEmail);
		Member receiver = findMember(receiverEmail);
		log.debug("채팅방 사용자 : {} , {}", user.getName(), receiver.getName());

		return chattingRoomRepository.findRoomByParticipants(userEmail, receiverEmail)
			.map(this::findExistingRoom)
			.orElseGet(() -> createNewChattingRoom(user, receiver));
	}

	//본인의 채팅방들을 찾는 메서드
	@Override
	public Page<ChattingRoomsResponseDto> findChattingRooms(String userEmail, String search, Pageable pageable) {
		Page<ChattingRoom> chattingRoomsPage = chattingRoomRepository.findChattingRooms(userEmail, search, pageable);
		Map<String, Message> messageMap = getMessageMap(getLatestMessages(getRoomIds(chattingRoomsPage)));
		return new PageImpl<>(mapToChattingRoomsResponseDto(userEmail, chattingRoomsPage, messageMap), pageable,
			chattingRoomsPage.getTotalElements());
	}

	//채팅내역 받기
	@Override
	public List<ChattingResponseDto> getChattingMessages(String roomId) {
		return messageRepository.findMessagesByRoom(roomId).stream()
			.map(message -> ChattingResponseDto.of(message, findMember(message.getChattingRoomInfo().senderEmail())))
			.collect(Collectors.toList());
	}

	//채팅방 나가기
	@Override
	@Transactional
	public void leaveChattingRoom(String userEmail, String roomId) {
		ChattingRoom chattingRoom = findChattingRoom(roomId);
		updateActivateParticipant(userEmail, chattingRoom);
		if (checkParticipants(chattingRoom)) {
			removeChattingRoom(chattingRoom);
		}
	}

	@Override
	public ReceiverResponseDto getReceiverEmail(String roomId, String userEmail) {
		return ReceiverResponseDto.of(getReceiver(userEmail, findChattingRoom(roomId)).getEmail());
	}

	private CreateChattingRoomResponseDto findExistingRoom(ChattingRoom chattingRoom) {
		return reactivateChatRoom(chattingRoom)
			? CreateChattingRoomResponseDto.of(chattingRoom.getId(), "RECONNECT_CHATTING_ROOM")
			: CreateChattingRoomResponseDto.of(chattingRoom.getId(), "EXIST_CHATTING_ROOM");
	}

	private CreateChattingRoomResponseDto createNewChattingRoom(Member user, Member receiver) {
		ChattingRoom chattingRoom = addParticipants(user, receiver);
		ChattingRoom savedRoom = chattingRoomRepository.save(chattingRoom);
		log.debug("채팅방 생성됨 : {}", savedRoom.getParticipantEmails());
		return CreateChattingRoomResponseDto.of(savedRoom.getId(), "NEW_CHATTING_ROOM");
	}

	//채팅방 나간사람 다시 들어가는 메서드
	private boolean reactivateChatRoom(ChattingRoom chattingRoom) {
		return chattingRoom.getParticipants().stream()
			.filter(participant -> !participant.isActive())
			.peek(ChattingRoomParticipant::updateActive)
			.findAny()
			.isPresent();
	}

	//채팅방 나갈때 쓰이는 메서드
	private static void updateActivateParticipant(String userEmail, ChattingRoom chattingRoom) {
		chattingRoom.getParticipants().stream()
			.filter(participant -> participant.getMember().getEmail().equals(userEmail))
			.findFirst()
			.ifPresent(ChattingRoomParticipant::updateActive);
	}

	private boolean checkParticipants(ChattingRoom chattingRoom) {
		return chattingRoom.getParticipants().stream().noneMatch(ChattingRoomParticipant::isActive);
	}

	private void removeChattingRoom(ChattingRoom chattingRoom) {
		chattingRoom.deleteRoom();
		chattingRoom.getParticipants().clear();
	}

	private ChattingRoom findChattingRoom(String roomId) {
		ChattingRoom chattingRoom = chattingRoomRepository.findById(roomId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CHAT_ROOM));
		log.debug("채팅방을 찾았습니다 : {}", chattingRoom.getId());
		return chattingRoom;
	}

	private List<ChattingRoomsResponseDto> mapToChattingRoomsResponseDto(String userEmail,
		Page<ChattingRoom> chattingRoomsPage,
		Map<String, Message> messageMap) {
		return chattingRoomsPage.getContent().stream()
			.map(chattingRoom -> {
				String roomId = chattingRoom.getId();
				Message lastMessage = messageMap.get(roomId);
				Member receiver = getReceiver(userEmail, chattingRoom);
				return ChattingRoomsResponseDto.of(roomId, receiver, lastMessage);
			})
			.sorted(Comparator.comparing(ChattingRoomsResponseDto::lastMessageTime,
				Comparator.nullsFirst(Comparator.reverseOrder())))
			.toList();
	}

	private List<Message> getLatestMessages(List<String> roomIds) {
		return messageRepository.findLatestMessages(roomIds);
	}

	private Member getReceiver(String userEmail, ChattingRoom chattingRoom) {
		return chattingRoom.getParticipants().stream()
			// 채팅방 참여자중 상대 참여자를 가져오는 필터
			.filter(participant -> !participant.getMember().getEmail().equals(userEmail))
			.findFirst()
			.orElseThrow(() -> {
				return new CustomException(ErrorCode.MEMBER_NOT_FOUND);
			})
			.getMember();
	}

	//roomId를 키값으로하고 message를 value로하는 map
	private Map<String, Message> getMessageMap(List<Message> latestMessages) {
		return latestMessages.stream()
			.collect(Collectors.toMap(message -> message.getChattingRoomInfo().roomId(), message -> message));
	}

	private List<String> getRoomIds(Page<ChattingRoom> chattingRoomsPage) {
		return chattingRoomsPage.getContent().stream()
			.map(ChattingRoom::getId)
			.toList();
	}

	private ChattingRoom addParticipants(Member sender, Member receiver) {
		ChattingRoom chattingRoom = ChattingRoom.create();
		chattingRoom.getParticipants().addAll(Set.of(
			ChattingRoomParticipant.create(chattingRoom, sender),
			ChattingRoomParticipant.create(chattingRoom, receiver)
		));
		return chattingRoom;
	}

	private Member findMember(String email) {
		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
		log.debug("유저를 찾았습니다. email ={}", member.getEmail());
		return member;
	}
}

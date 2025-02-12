package com.tripmarket.domain.chatting.service;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
	private final RedisChattingService redisChattingService;

	// 채팅방을 생성하며 각 상황에 맞게 status분리
	@Override
	@Transactional
	public CreateChattingRoomResponseDto create(String userEmail, String receiverEmail) {
		Member user = findMember(userEmail);
		Member receiver = findMember(receiverEmail);
		Optional<ChattingRoom> optionalChattingRoom = chattingRoomRepository.findRoomByParticipants(userEmail,
			receiverEmail);
		if (optionalChattingRoom.isPresent()) {
			ChattingRoom chattingRoom = optionalChattingRoom.get();
			boolean isLeftRoom = updateActivate(chattingRoom);
			if (isLeftRoom) {
				return CreateChattingRoomResponseDto.of(
					chattingRoom.getId(), "RECONNECT_CHATTING_ROOM");
			} else {
				return CreateChattingRoomResponseDto.of(
					chattingRoom.getId(), "EXIST_CHATTING_ROOM");
			}
		} else {
			ChattingRoom chattingRoom = addParticipants(user, receiver);
			ChattingRoom save = chattingRoomRepository.save(chattingRoom);
			log.debug("채팅방이 생성되었습니다. 채팅방 참여자: {}", String.join(", ", save.getParticipantEmails()));
			return CreateChattingRoomResponseDto.of(
				save.getId(), "NEW_CHATTING_ROOM");
		}
	}

	//본인의 채팅방들을 찾는 메서드
	@Override
	public Page<ChattingRoomsResponseDto> findChattingRooms(String userEmail, String search, Pageable pageable) {

		Page<ChattingRoom> chattingRoomsPage = findChattingRoomsPage(userEmail, search, pageable);

		List<String> roomIds = findRoomIds(chattingRoomsPage);

		List<Message> latestMessages = messageRepository.findLatestMessages(roomIds);

		Map<String, Message> messageMap = getMessageMap(latestMessages);

		List<ChattingRoomsResponseDto> chattingRoomsResponse = getChattingRoomsResponse(userEmail, chattingRoomsPage,
			messageMap);

		return new PageImpl<>(chattingRoomsResponse, pageable, chattingRoomsPage.getTotalElements());
	}

	//채팅내역 받기
	@Override
	public List<ChattingResponseDto> getChattingMessages(String roomId) {
		List<Message> messages = messageRepository.findMessagesByRoom(roomId);

		return getMessages(messages);
	}

	//채팅방 나가기
	@Override
	@Transactional
	public void leaveChattingRoom(String userEmail, String roomId) {
		ChattingRoom chattingRoom = findChattingRoom(roomId);
		updateActivateParticipant(userEmail, chattingRoom);
		boolean checkParticipants = chattingRoom.getParticipants().stream()
			.noneMatch(ChattingRoomParticipant::isActive);
		if (checkParticipants) {
			chattingRoom.deleteRoom();
			chattingRoom.getParticipants().clear();
		}
	}

	@Override
	@Transactional
	public void markMessagesAsRead(String roomId, String userEmail) {
		List<Message> unreadMessages = messageRepository.findUnreadMessages(roomId, userEmail);

		for (Message message : unreadMessages) {
			message.updateRead(true);
			log.info("Updating message read status for ID: {}", message.getId());
		}

		messageRepository.saveAll(unreadMessages);  // 저장

		// 확인용으로 다시 읽어와서 readStatus가 바뀌었는지 검사
		List<Message> updatedMessages = messageRepository.findUnreadMessages(roomId, userEmail);
		if (updatedMessages.isEmpty()) {
			log.info("All messages marked as read successfully.");
		} else {
			log.error("Unread messages still exist, possible save issue.");
		}

		redisChattingService.resetUnreadCount(roomId, userEmail);
	}

	@Override
	public ReceiverResponseDto getReceiverEmail(String roomId, String userEmail) {
		ChattingRoom chattingRoom = findChattingRoom(roomId);
		Member receiver = getReceiver(userEmail, chattingRoom);
		return ReceiverResponseDto.of(receiver.getEmail());
	}

	// 채팅방 한명만 나갔을경우 다시 기존방에 들어가는 메서드
	private static boolean updateActivate(ChattingRoom chattingRoom) {
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

	private ChattingRoom findChattingRoom(String roomId) {
		ChattingRoom chattingRoom = chattingRoomRepository.findById(roomId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CHAT_ROOM));
		log.debug("채팅방을 찾았습니다 : {}", chattingRoom.getId());
		return chattingRoom;
	}

	private List<ChattingResponseDto> getMessages(List<Message> messages) {
		return messages.stream().map(message -> {
			Member sender = memberRepository.findByEmail(message.getSender())
				.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
			return ChattingResponseDto.of(message, sender);
		}).collect(Collectors.toList());
	}

	private List<ChattingRoomsResponseDto> getChattingRoomsResponse(String userEmail,
		Page<ChattingRoom> chattingRoomsPage,
		Map<String, Message> messageMap) {
		return chattingRoomsPage.getContent().stream()
			.map(chattingRoom -> {
				String roomId = chattingRoom.getId();
				Message lastMessage = messageMap.get(roomId);
				Member receiver = getReceiver(userEmail, chattingRoom);
				int unreadCount = redisChattingService.getUnreadCount(roomId, userEmail);

				return ChattingRoomsResponseDto.of(roomId, receiver, lastMessage, unreadCount);
			})
			//최근에 도착한 메세지 순서대로 정렬
			.sorted(Comparator.comparing(ChattingRoomsResponseDto::lastMessageTime,
				Comparator.nullsFirst(Comparator.reverseOrder())))
			.toList();
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
		return latestMessages.stream().collect(Collectors.toMap(Message::getRoomId, message -> message));
	}

	private List<String> findRoomIds(Page<ChattingRoom> chattingRoomsPage) {
		return chattingRoomsPage.getContent().stream()
			.map(ChattingRoom::getId)
			.toList();
	}

	private Page<ChattingRoom> findChattingRoomsPage(String userEmail, String search, Pageable pageable) {
		return chattingRoomRepository.findChattingRooms(userEmail, search, pageable);
	}

	private ChattingRoom addParticipants(Member sender, Member receiver) {
		ChattingRoom chattingRoom = ChattingRoom.create();
		Set<ChattingRoomParticipant> participants = new HashSet<>();
		participants.add(ChattingRoomParticipant.create(chattingRoom, sender));
		participants.add(ChattingRoomParticipant.create(chattingRoom, receiver));
		chattingRoom.getParticipants().addAll(participants);
		return chattingRoom;
	}

	private Member findMember(String email) {
		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
		log.debug("유저를 찾았습니다. email ={}", member.getEmail());
		return member;
	}
}

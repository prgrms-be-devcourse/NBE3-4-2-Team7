package com.tripmarket.domain.chatting.service;

import java.util.Set;

import com.tripmarket.domain.chatting.dto.ChattingResponseDto;
import com.tripmarket.domain.chatting.dto.MessageDto;
import com.tripmarket.domain.chatting.entity.ChattingRoom;
import com.tripmarket.domain.chatting.entity.Message;
import com.tripmarket.domain.chatting.repository.chattingroom.ChattingRoomRepository;
import com.tripmarket.domain.chatting.repository.message.MessageRepository;
import com.tripmarket.domain.member.entity.Member;
import com.tripmarket.domain.member.repository.MemberRepository;
import com.tripmarket.global.exception.CustomException;
import com.tripmarket.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

	private final MessageRepository messageRepository;
	private final SimpMessagingTemplate simpMessagingTemplate;
	private final RedisChattingService redisChattingService;
	private final MemberRepository memberRepository;
	private final ChattingRoomRepository chattingRoomRepository;

	@Transactional
	@Override
	public void sendMessage(MessageDto messageDto, String roomId) {
		log.info("메시지 전송 시도: sender={}, receiver={}, roomId={}", messageDto.sender(), messageDto.receiver(), roomId);

		// 메시지 저장
		Message message = messageDto.toMessageEntity();
		message.updateRead(false);  // 기본적으로 안 읽음 상태로 저장

		Set<Object> connectedUsers = redisChattingService.getUsersByChattingRoom(roomId);
		boolean isReceiverConnected = connectedUsers.contains(messageDto.receiver());

		if (isReceiverConnected) {
			message.updateRead(true);  // 수신자가 접속 중이면 읽음 처리
			redisChattingService.resetUnreadCount(roomId, messageDto.receiver()); // Redis 초기화
		} else {
			redisChattingService.addUnreadCount(roomId, messageDto.receiver()); // 읽지 않은 메시지 수 증가
		}

		Message save = messageRepository.save(message);
		Member member = getMember(save);
		ChattingResponseDto chattingResponseDto = ChattingResponseDto.of(save, member);

		ChattingRoom chattingRoom = getChattingRoom(roomId);

		//상대가 채팅방나간경우 다시 상대에게 채팅이 보이게처리
		reconnectChattingRoom(messageDto, chattingRoom);

		// STOMP 브로커로 메시지 전송
		String destination = "/topic/chat.room." + roomId;
		try {
			simpMessagingTemplate.convertAndSend(destination, chattingResponseDto);
		} catch (Exception e) {
			throw new CustomException(ErrorCode.FAIL_MESSAGE_SEND);
		}
	}

	private static void reconnectChattingRoom(MessageDto messageDto, ChattingRoom chattingRoom) {
		chattingRoom.getParticipants().forEach(participant -> {
			if (participant.getMember().getEmail().equals(messageDto.receiver()) && !participant.isActive()) {
				participant.updateActive();
			}
		});
	}

	private ChattingRoom getChattingRoom(String roomId) {
		return chattingRoomRepository.findById(roomId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CHAT_ROOM));
	}

	private Member getMember(Message save) {
		return memberRepository.findByEmail(save.getSender())
			.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
	}
}

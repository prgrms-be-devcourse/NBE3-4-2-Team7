package com.tripmarket.domain.chatting.service;

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

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

	private final MessageRepository messageRepository;
	private final SimpMessagingTemplate simpMessagingTemplate;
	private final MemberRepository memberRepository;
	private final ChattingRoomRepository chattingRoomRepository;

	@Override
	public void sendMessage(MessageDto messageDto, String roomId) {
		log.debug("메시지 전송 시도: sender={}, receiver={}, roomId={}",
			messageDto.sender(), messageDto.receiver(), roomId);

		try {
			Message savedMessage = saveMessage(messageDto);
			updateChattingRoomStatus(messageDto, roomId);
			sendChatMessage(savedMessage);
		} catch (Exception e) {
			log.error("메시지 전송 실패: roomId={}, sender={}, receiver={}", roomId, messageDto.sender(),
				messageDto.receiver(), e);
			rollbackMessage(messageDto.sender(), roomId);
			throw new CustomException(ErrorCode.FAIL_MESSAGE_SEND);
		}
	}

	private void rollbackMessage(String sender, String roomId) {
		log.debug("롤백 트랜잭션 실행: roomId={}, sender={}", roomId, sender);
		messageRepository.deleteMessage(roomId, sender);
	}

	private Message saveMessage(MessageDto messageDto) {
		Message message = messageDto.toMessageEntity();
		return messageRepository.save(message);
	}

	private void updateChattingRoomStatus(MessageDto messageDto, String roomId) {
		ChattingRoom chattingRoom = getChattingRoom(roomId);
		reconnectChattingRoom(messageDto, chattingRoom);
	}

	private void sendChatMessage(Message message) {
		ChattingResponseDto responseDto = ChattingResponseDto.of(message, getMember(message));
		String destination = "/topic/chat.room." + message.getChattingRoomInfo().roomId();

		try {
			simpMessagingTemplate.convertAndSend(destination, responseDto);
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
		return memberRepository.findByEmail(save.getChattingRoomInfo().senderEmail())
			.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
	}
}

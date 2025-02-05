package com.tripmarket.domain.chatting.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tripmarket.domain.chatting.dto.ChattingRoomRequestDto;
import com.tripmarket.domain.chatting.entity.ChattingRoom;
import com.tripmarket.domain.chatting.repository.ChattingRoomRepository;
import com.tripmarket.domain.member.repository.MemberRepository;
import com.tripmarket.global.exception.CustomException;
import com.tripmarket.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChattingRoomService {

	private final ChattingRoomRepository chattingRoomRepository;
	private final MemberRepository memberRepository;

	//Todo 사용자 토큰인증 완료되면 수정 예정
	@Transactional
	public void create(ChattingRoomRequestDto chattingRoomRequestDto) {
		validateMembersExist(chattingRoomRequestDto);
		validateChattingRoom(chattingRoomRequestDto);
		ChattingRoom chattingRoom = chattingRoomRequestDto.toChattingRoomEntity();
		ChattingRoom save = chattingRoomRepository.save(chattingRoom);
		log.info("채팅방이 생성되었습니다. ID : {}, user1 : {}, user2 : {} ", save.getId(), save.getUser1Id(),
			save.getUser2Id());
	}

	//Todo jwt토큰 인증해서 사용자 정보가져오게되면 메서드 수정 예정
	private void validateMembersExist(ChattingRoomRequestDto chattingRoomRequestDto) {
		if (!memberRepository.existsById(chattingRoomRequestDto.getUser1Id()) ||
			!memberRepository.existsById(chattingRoomRequestDto.getUser2Id())) {
			throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
		}
	}

	private void validateChattingRoom(ChattingRoomRequestDto chattingRoomRequestDto) {
		chattingRoomRepository.findByUser1IdAndUser2Id(
			chattingRoomRequestDto.getUser1Id(),
			chattingRoomRequestDto.getUser2Id()
		).ifPresent(chatRoom -> {
			throw new CustomException(ErrorCode.DUPLICATE_CHAT_ROOM);
		});
	}
}

package com.tripmarket.domain.chatting.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import com.tripmarket.domain.chatting.dto.ChattingResponseDto;
import com.tripmarket.domain.chatting.dto.MessageDto;
import com.tripmarket.domain.chatting.entity.ChattingRoom;
import com.tripmarket.domain.chatting.entity.ChattingRoomParticipant;
import com.tripmarket.domain.chatting.entity.Message;
import com.tripmarket.domain.chatting.repository.chattingroom.ChattingRoomRepository;
import com.tripmarket.domain.chatting.repository.message.MessageRepository;
import com.tripmarket.domain.member.entity.Member;
import com.tripmarket.domain.member.repository.MemberRepository;

class MessageServiceImplTest {

	@Mock
	private MessageRepository messageRepository;
	@Mock
	private SimpMessagingTemplate simpMessagingTemplate;
	@Mock
	private MemberRepository memberRepository;
	@Mock
	private ChattingRoomRepository chattingRoomRepository;

	@InjectMocks
	private MessageServiceImpl messageService;

	private Member sender, receiver;
	private ChattingRoom chattingRoom;
	private MessageDto messageDto;
	private Message message;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		createTestFixtures();
	}

	@Test
	void sendMessageSuccess() {
		// Given
		given(chattingRoomRepository.findById(chattingRoom.getId()))
			.willReturn(Optional.of(chattingRoom));

		given(memberRepository.findByEmail(sender.getEmail()))
			.willReturn(Optional.of(sender));

		given(messageRepository.save(any(Message.class)))
			.willAnswer(invocation -> {
				Message msg = invocation.getArgument(0);
				ReflectionTestUtils.setField(msg, "id", new ObjectId());
				return msg;
			});

		// When
		messageService.sendMessage(messageDto, chattingRoom.getId());

		// Then
		ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
		verify(messageRepository).save(messageCaptor.capture());

		Message savedMessage = messageCaptor.getValue();

		assertThat(savedMessage).isNotNull();
		assertThat(savedMessage.getRoomId()).isEqualTo(chattingRoom.getId());
		assertThat(savedMessage.getChattingRoomInfo().senderEmail()).isEqualTo(messageDto.sender());
		assertThat(savedMessage.getChattingRoomInfo().receiverEmail()).isEqualTo(messageDto.receiver());
		assertThat(savedMessage.getContent()).isEqualTo(messageDto.content());

		ArgumentCaptor<ChattingResponseDto> responseCaptor = ArgumentCaptor.forClass(ChattingResponseDto.class);
		verify(simpMessagingTemplate).convertAndSend(eq("/topic/chat.room." + chattingRoom.getId()), responseCaptor.capture());

		ChattingResponseDto sentMessageDto = responseCaptor.getValue();

		assertThat(sentMessageDto).isNotNull();
		assertThat(sentMessageDto.content()).isEqualTo(messageDto.content());
		assertThat(sentMessageDto.senderName()).isEqualTo(sender.getName());

		verify(messageRepository, times(1)).save(any(Message.class));
		verify(simpMessagingTemplate, times(1)).convertAndSend(
			eq("/topic/chat.room." + chattingRoom.getId()), any(ChattingResponseDto.class));
	}


	private void createTestFixtures() {
		sender = createMember("강정수", "sender@gmail.com");
		receiver = createMember("강수정", "receiver@gmail.com");
		chattingRoom = createChattingRoom(sender, receiver);
		messageDto = createMessageDto(chattingRoom, sender, receiver);
		message = messageDto.toMessageEntity();
		ReflectionTestUtils.setField(message, "id", new ObjectId()); // 메시지 ID 설정
	}

	private Member createMember(String name, String email) {
		return Member.builder().name(name).email(email).build();
	}

	private ChattingRoom createChattingRoom(Member sender, Member receiver) {
		ChattingRoom room = ChattingRoom.create();
		room.getParticipants().addAll(List.of(
			ChattingRoomParticipant.create(room, sender),
			ChattingRoomParticipant.create(room, receiver)
		));
		return room;
	}

	private MessageDto createMessageDto(ChattingRoom chattingRoom, Member sender, Member receiver) {
		return MessageDto.builder()
			.roomId(chattingRoom.getId())
			.sender(sender.getEmail())
			.receiver(receiver.getEmail())
			.content("ㅎㅇ")
			.build();
	}
}
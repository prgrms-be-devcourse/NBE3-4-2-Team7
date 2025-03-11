package com.tripmarket.domain.chatting.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

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

class ChattingRoomServiceImplTest {

	@Mock
	private ChattingRoomRepository chattingRoomRepository;

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private MessageRepository messageRepository;

	@InjectMocks
	private ChattingRoomServiceImpl chattingRoomService;

	private Member member1, member2, member3, member4;
	private ChattingRoom chattingRoom1, chattingRoom2, chattingRoom3;
	private Message message1, message3, message4, message5, message2;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		createMembers();
		createChattingRooms();
		createMessages();
	}

	//채팅방이 존재하지 않을떄
	@Test
	void createNewChattingRoom() {
		// Given
		MockFindMemberByEmail("test@gmail.com", member1);
		MockFindMemberByEmail("test2@gmail.com", member2);
		given(chattingRoomRepository.findRoomByParticipants(member1.getEmail(), member2.getEmail()))
			.willReturn(Optional.empty());
		given(chattingRoomRepository.save(any(ChattingRoom.class))).willReturn(chattingRoom1);

		// When
		CreateChattingRoomResponseDto response = chattingRoomService.create(member1.getEmail(), member2.getEmail());

		// Then
		assertThat(response).isNotNull();
		assertThat(response.roomId()).isEqualTo(chattingRoom1.getId());
		assertThat(response.status()).isEqualTo("NEW_CHATTING_ROOM");

		verify(chattingRoomRepository, times(1)).save(any(ChattingRoom.class));
	}

	//이미 기존에 채팅방이있는데 한명이 나갔을 경우 - 한명 다시 채팅방으로 연결
	@Test
	void reconnectChattingRoom() {
		// Given
		MockFindMemberByEmail("test@gmail.com", member1);
		MockFindMemberByEmail("test2@gmail.com", member2);
		given(chattingRoomRepository.findRoomByParticipants(member1.getEmail(), member2.getEmail()))
			.willReturn(Optional.of(chattingRoom1));
		// 한명 비활성화 상태
		chattingRoom1.getParticipants().get(0).updateActive();

		// When
		CreateChattingRoomResponseDto response = chattingRoomService.create(member1.getEmail(), member2.getEmail());

		// Then
		assertThat(response).isNotNull();
		assertThat(response.roomId()).isEqualTo(chattingRoom1.getId());
		assertThat(response.status()).isEqualTo("RECONNECT_CHATTING_ROOM");

		verify(chattingRoomRepository, never()).save(any(ChattingRoom.class));
	}

	//기존 채팅방이 존재하며 둘다 나가지 않았을 경우 - 이떄는 기존 채팅방 그대로 유지
	@Test
	void existChattingRoom() {
		// Given
		MockFindMemberByEmail("test@gmail.com", member1);
		MockFindMemberByEmail("test2@gmail.com", member2);
		given(chattingRoomRepository.findRoomByParticipants(member1.getEmail(), member2.getEmail()))
			.willReturn(Optional.of(chattingRoom1));

		// When
		CreateChattingRoomResponseDto response = chattingRoomService.create(member1.getEmail(), member2.getEmail());

		// Then
		assertThat(response).isNotNull();
		assertThat(response.roomId()).isEqualTo(chattingRoom1.getId());
		assertThat(response.status()).isEqualTo("EXIST_CHATTING_ROOM");

		verify(chattingRoomRepository, never()).save(any(ChattingRoom.class));
	}

	// 검색어 없이 사용자의 채팅방 목록 조회하는 경우
	@Test
	void findChattingRoomsWithoutSearchKeyword() {
		// Given
		MockFindMemberByEmail("test@gmail.com", member1);

		List<ChattingRoom> chattingRooms = List.of(chattingRoom1, chattingRoom2, chattingRoom3);
		Page<ChattingRoom> chattingRoomsPage = new PageImpl<>(chattingRooms);

		given(chattingRoomRepository.findChattingRooms(eq(member1.getEmail()), anyString(), any(Pageable.class)))
			.willReturn(chattingRoomsPage);

		List<Message> messages = List.of(message5, message4, message3);
		given(messageRepository.findLatestMessages(anyList())).willReturn(messages);

		String searchKeyword = "";
		Pageable pageable = PageRequest.of(0, 10);

		// When
		Page<ChattingRoomsResponseDto> result = chattingRoomService.findChattingRooms(member1.getEmail(),
			searchKeyword, pageable);

		// Then
		assertThat(result).isNotNull();
		assertThat(result.getContent()).hasSize(3);
		// 최신순으로 나오는지 검증
		assertThat(result.getContent().get(0).lastMessage()).isEqualTo(message5.getContent());
		assertThat(result.getContent().get(1).lastMessage()).isEqualTo(message4.getContent());
		assertThat(result.getContent().get(2).lastMessage()).isEqualTo(message3.getContent());

		verify(chattingRoomRepository, times(1)).findChattingRooms(eq(member1.getEmail()), anyString(),
			any(Pageable.class));
		verify(messageRepository, times(1)).findLatestMessages(anyList());
	}

	// 특정검색어로 검색했을때 채팅 목록 조회하는 경우
	@Test
	void findChattingRoomsWithSearchKeyword() {
		// Given
		MockFindMemberByEmail("test@gmail.com", member1);

		String searchKeyword = "강수정";
		Pageable pageable = PageRequest.of(0, 10);

		given(chattingRoomRepository.findChattingRooms(eq(member1.getEmail()), eq(searchKeyword), any(Pageable.class)))
			.willReturn(new PageImpl<>(List.of(chattingRoom1)));

		List<Message> messages = List.of(message5);
		given(messageRepository.findLatestMessages(anyList())).willReturn(messages);

		// When
		Page<ChattingRoomsResponseDto> result = chattingRoomService.findChattingRooms(member1.getEmail(),
			searchKeyword, pageable);

		// Then
		assertThat(result).isNotNull();
		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().get(0).lastMessage()).contains(message5.getContent());

		verify(chattingRoomRepository, times(1)).findChattingRooms(eq(member1.getEmail()), eq(searchKeyword),
			any(Pageable.class));
		verify(messageRepository, times(1)).findLatestMessages(anyList());
	}

	// 채팅방 들어가서 메세지 조회하는 경우
	@Test
	void getChattingMessages() {
		given(messageRepository.findMessagesByRoom(chattingRoom1.getId())).willReturn(
			List.of(message1, message2, message5));

		MockFindMemberByEmail(member1.getEmail(), member1);
		MockFindMemberByEmail(member2.getEmail(), member2);

		// When
		List<ChattingResponseDto> response = chattingRoomService.getChattingMessages(chattingRoom1.getId());

		// Then
		assertThat(response).isNotNull();
		assertThat(response).hasSize(3);

		// 메세지 순서 검증
		assertThat(response.get(0).content()).isEqualTo(message1.getContent());
		assertThat(response.get(1).content()).isEqualTo(message2.getContent());
		assertThat(response.get(2).content()).isEqualTo(message5.getContent());

		// 발신자 검증
		assertThat(response.get(0).senderName()).isEqualTo(member1.getName());
		assertThat(response.get(1).senderName()).isEqualTo(member2.getName());
		assertThat(response.get(2).senderName()).isEqualTo(member1.getName());

		verify(messageRepository, times(1)).findMessagesByRoom(chattingRoom1.getId());

		verify(memberRepository, times(1)).findByEmail(member1.getEmail());
		verify(memberRepository, times(1)).findByEmail(member2.getEmail());
	}

	// 채팅방 나갈때 -한명만 나가서 채팅방은 그대로 유지
	@Test
	void leaveChattingRoomOneUser() {
		// Given
		MockFindChattingRoom(chattingRoom1);

		// When
		chattingRoomService.leaveChattingRoom(member1.getEmail(), chattingRoom1.getId());

		// Then
		// member1 비활성화인지 검증
		assertThat(chattingRoom1.getParticipants().stream()
			.filter(p -> p.getMember().getEmail().equals(member1.getEmail()))
			.findFirst().get().isActive()).isFalse();
		assertThat(chattingRoom1.isDelete()).isFalse();
		assertThat(chattingRoom1.getDeleteDate()).isNull();

		verify(chattingRoomRepository, never()).delete(chattingRoom1);
	}

	// 채팅방을 나갈때 - 두명 다 나가서 채팅방이 일주일후 삭제 처리
	@Test
	void leaveChattingRoomTwoUsers() {
		// Given
		MockFindChattingRoom(chattingRoom1);

		// When
		chattingRoomService.leaveChattingRoom(member1.getEmail(), chattingRoom1.getId());
		chattingRoomService.leaveChattingRoom(member2.getEmail(), chattingRoom1.getId());

		// Then
		assertThat(chattingRoom1.isDelete()).isTrue();
		assertThat(chattingRoom1.getDeleteDate()).isNotNull();
		assertThat(chattingRoom1.getDeleteDate()).isAfter(LocalDateTime.now());

		verify(chattingRoomRepository, never()).delete(chattingRoom1);
	}

	// 상대를 조회 하는 경우
	@Test
	void getReceiverEmail() {
		// Given
		MockFindChattingRoom(chattingRoom1);

		String sender = member1.getEmail();
		String receiver = member2.getEmail();

		// When
		ReceiverResponseDto response = chattingRoomService.getReceiverEmail(chattingRoom1.getId(), sender);

		// Then
		assertThat(response).isNotNull();
		assertThat(response.receiver()).isEqualTo(receiver);
		verify(chattingRoomRepository, times(1)).findById(chattingRoom1.getId());
	}

	// 채팅방 Mock
	private void MockFindChattingRoom(ChattingRoom chattingRoom) {
		given(chattingRoomRepository.findById(chattingRoom.getId())).willReturn(Optional.of(chattingRoom));
	}

	// 사용자 Mock
	private void MockFindMemberByEmail(String email, Member member) {
		given(memberRepository.findByEmail(email)).willReturn(Optional.of(member));
	}

	private void createMembers() {
		member1 = createMember("강정수", "test@gmail.com");
		member2 = createMember("강수정", "test2@gmail.com");
		member3 = createMember("정수강", "test3@gmail.com");
		member4 = createMember("정강수", "test4@gmail.com");
	}

	private void createChattingRooms() {
		chattingRoom1 = createChattingRoom(member1, member2, "adadawojdaodjoa");
		chattingRoom2 = createChattingRoom(member1, member3, "adadawojdaodjoadawa");
		chattingRoom3 = createChattingRoom(member1, member4, "adadawojdaodjoa21");
	}

	private void createMessages() {
		message1 = createMessage(chattingRoom1, member1, member2, "ㅎㅇ요");
		message2 = createMessage(chattingRoom1, member2, member1, "나도 보냄");
		message3 = createMessage(chattingRoom2, member1, member3, "ㅎㅇㅎㅇㅎ요");
		message4 = createMessage(chattingRoom3, member1, member4, "ㅎㅇㅎㅇㅎㅇㅎㅇㅎㅇ");
		message5 = createMessage(chattingRoom1, member1, member2, "이게 최신");
	}

	private Member createMember(String name, String email) {
		return Member.builder()
			.name(name)
			.email(email)
			.build();
	}

	private ChattingRoom createChattingRoom(Member sender, Member receiver, String roomId) {
		ChattingRoom room = ChattingRoom.create();
		ReflectionTestUtils.setField(room, "id", roomId);
		room.getParticipants().addAll(List.of(
			ChattingRoomParticipant.create(room, sender),
			ChattingRoomParticipant.create(room, receiver)
		));
		return room;
	}

	private Message createMessage(ChattingRoom room, Member sender, Member receiver, String content) {
		return Message.builder()
			.id(new ObjectId())
			.roomId(room.getId())
			.content(content)
			.chattingRoomInfo(new Message.ChattingRoomInfo(sender.getEmail(), receiver.getEmail()))
			.build();
	}
}
package com.tripmarket.domain.chatting.repository.chattingroom;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tripmarket.domain.chatting.entity.ChattingRoom;
import com.tripmarket.domain.chatting.entity.QChattingRoom;
import com.tripmarket.domain.chatting.entity.QChattingRoomParticipant;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChattingRoomRepositoryImpl implements CustomChattingRoomRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public boolean findRoomByParticipants(String email1, String email2) {
		QChattingRoom chattingRoom = QChattingRoom.chattingRoom;
		QChattingRoomParticipant participant1 = new QChattingRoomParticipant("participant1");
		QChattingRoomParticipant participant2 = new QChattingRoomParticipant("participant2");

		BooleanExpression condition = (participant1.member.email.eq(email1).and(participant2.member.email.eq(email2)))
			.or(participant1.member.email.eq(email2).and(participant2.member.email.eq(email1)));

		return queryFactory.selectOne()
			.from(chattingRoom)
			.join(chattingRoom.participants, participant1)
			.join(chattingRoom.participants, participant2)
			.where(condition)
			.fetchOne() != null;
	}

	@Override
	public Page<ChattingRoom> findChattingRooms(String userEmail, String search, Pageable pageable) {
		QChattingRoom chattingRoom = QChattingRoom.chattingRoom;
		QChattingRoomParticipant participant = QChattingRoomParticipant.chattingRoomParticipant;

		BooleanBuilder condition = new BooleanBuilder();
		condition.and(participant.member.email.eq(userEmail));

		if (search != null && !search.isEmpty()) {
			condition.and(chattingRoom.participants.any().member.name.containsIgnoreCase(search));
		}

		List<ChattingRoom> chattingRooms = queryFactory.selectFrom(chattingRoom)
			.join(chattingRoom.participants, participant)
			.where(condition)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		long total = queryFactory.selectFrom(chattingRoom)
			.join(chattingRoom.participants, participant)
			.where(condition)
			.fetchCount();

		// Page로 변환해서 반환
		return new PageImpl<>(chattingRooms, pageable, total);
	}
}

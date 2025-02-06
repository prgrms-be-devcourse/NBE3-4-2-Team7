package com.tripmarket.domain.chatting.repository.chattingroom;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
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
}

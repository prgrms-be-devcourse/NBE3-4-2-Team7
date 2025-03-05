package com.tripmarket.domain.chatting.repository.message;

import lombok.RequiredArgsConstructor;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.domain.Sort;

import com.tripmarket.domain.chatting.entity.Message;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class MessageRepositoryImpl implements CustomMessageRepository {

	private final MongoTemplate mongoTemplate;

	@Override
	public List<Message> findLatestMessages(List<String> roomIds) {
		return roomIds.stream()
			.map(roomId -> {
				Query query = new Query()
					.addCriteria(Criteria.where("chattingRoomInfo.roomId").is(roomId))
					.with(Sort.by(Sort.Direction.DESC, "_id"))
					.limit(1);
				return mongoTemplate.findOne(query, Message.class);
			})
			.filter(Objects::nonNull)
			.collect(Collectors.toList());
	}

	@Override
	public List<Message> findMessagesByRoom(String roomId) {
		Query query = new Query()
			.addCriteria(Criteria.where("chattingRoomInfo.roomId").is(roomId))
			.with(Sort.by(Sort.Direction.ASC, "_id"));
		return mongoTemplate.find(query, Message.class);
	}

	@Override
	public void deleteByRoomId(String roomId) {
		Query query = new Query(Criteria.where("chattingRoomInfo.roomId").is(roomId));
		mongoTemplate.remove(query, Message.class);
	}

	@Override
	public void deleteMessage(String roomId, String sender) {
		Query query = new Query(Criteria.where("chattingRoomInfo.roomId").is(roomId)
			.and("chattingRoomInfo.senderEmail").is(sender));
		mongoTemplate.remove(query, Message.class);
	}
}

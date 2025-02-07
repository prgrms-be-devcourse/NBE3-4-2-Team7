package com.tripmarket.domain.chatting.repository.message;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
					.addCriteria(Criteria.where("roomId").is(roomId))
					.with(Sort.by(Sort.Direction.DESC, "createdAt"))
					.limit(1);
				return mongoTemplate.findOne(query, Message.class);
			})
			.filter(Objects::nonNull)
			.collect(Collectors.toList());
	}

	@Override
	public Page<Message> findMessagesByRoom(String roomId, Pageable pageable) {
		Query query = new Query()
			.addCriteria(Criteria.where("roomId").is(roomId))
			.with(Sort.by(Sort.Direction.DESC, "createdAt"))
			.with(pageable);

		List<Message> messages = mongoTemplate.find(query, Message.class);
		long count = mongoTemplate.count(Query.query(Criteria.where("roomId").is(roomId)), Message.class);

		return new PageImpl<>(messages, pageable, count);
	}
}

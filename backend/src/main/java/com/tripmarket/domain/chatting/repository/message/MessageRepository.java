package com.tripmarket.domain.chatting.repository.message;

import com.tripmarket.domain.chatting.entity.Message;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MessageRepository extends MongoRepository<Message, String>,CustomMessageRepository {
}

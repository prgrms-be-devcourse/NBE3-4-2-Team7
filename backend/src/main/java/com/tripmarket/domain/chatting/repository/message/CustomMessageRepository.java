package com.tripmarket.domain.chatting.repository.message;

import java.util.List;

import com.tripmarket.domain.chatting.entity.Message;


public interface CustomMessageRepository {
	List<Message> findLatestMessages(List<String> roomIds);
}

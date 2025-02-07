package com.tripmarket.domain.chatting.repository.message;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.tripmarket.domain.chatting.entity.Message;


public interface CustomMessageRepository {
	List<Message> findLatestMessages(List<String> roomIds);
	Page<Message> findMessagesByRoom(String roomId, Pageable pageable);
}

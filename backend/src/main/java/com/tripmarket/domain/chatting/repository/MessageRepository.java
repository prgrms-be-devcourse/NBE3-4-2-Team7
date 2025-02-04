package com.tripmarket.domain.chatting.repository;

import com.tripmarket.domain.chatting.entity.Message;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}

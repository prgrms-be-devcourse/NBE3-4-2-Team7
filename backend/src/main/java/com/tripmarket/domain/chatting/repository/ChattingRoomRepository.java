package com.tripmarket.domain.chatting.repository;

import com.tripmarket.domain.chatting.entity.ChattingRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ChattingRoomRepository extends JpaRepository<ChattingRoom, Long> {

	Optional<ChattingRoom> findByUser1IdAndUser2Id(Long user1Id, Long User2Id);
}
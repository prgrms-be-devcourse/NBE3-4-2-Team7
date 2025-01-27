package com.tripmarket.domain.chatting.entity;

import com.tripmarket.global.jpa.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@Entity
@RequiredArgsConstructor
@NoArgsConstructor
public class Message extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatting_room_id", nullable = false)
    private ChattingRoom chattingRoom; // 메시지가 속한 채팅방

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content; // 메시지 내용

}

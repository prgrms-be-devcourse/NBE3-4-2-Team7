package com.tripmarket.domain.chatting.entity;

import com.tripmarket.global.jpa.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class Message extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId; // 메시지 ID (Primary Key)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatting_room_id")
    private ChattingRoom chattingRoom; // 메시지가 속한 채팅방

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content; // 메시지 내용

    // 생성자
    public Message(ChattingRoom chattingRoom, String content) {
        this.chattingRoom = chattingRoom;
        this.content = content;
    }


}

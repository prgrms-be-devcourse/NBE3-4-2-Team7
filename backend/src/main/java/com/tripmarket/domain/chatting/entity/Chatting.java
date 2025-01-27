package com.tripmarket.domain.chatting.entity;

import com.tripmarket.domain.chatting.entity.ChattingRoom;
import com.tripmarket.domain.member.entity.Member;
import com.tripmarket.global.jpa.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class Chatting extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chattingId; // 채팅 ID (Primary Key)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatting_room_id")
    private ChattingRoom chattingRoom; // 연결된 채팅방

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Member user; // 채팅 참여 유저

    // 생성자
    public Chatting(ChattingRoom chattingRoom, Member user) {
        this.chattingRoom = chattingRoom;
        this.user = user;
    }


}
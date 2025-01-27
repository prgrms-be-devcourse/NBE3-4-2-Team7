package com.tripmarket.domain.chatting.entity;

import com.tripmarket.global.jpa.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class ChattingRoom extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private ChatStatus chatStatus; // 채팅방 상태 (ON/OFF)

    public enum ChatStatus {
        ON, OFF
    }


}

package com.tripmarket.domain.guide.entity;

import com.tripmarket.global.jpa.entity.BaseEntity;
import com.tripmarket.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Guide extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Member member; // 가이드와 연결된 회원 정보

    @Column(nullable = false, length = 100)
    private String name; // 가이드 이름

    @Column(length = 100)
    private String languages; // 가이드가 사용하는 언어

    @Column(length = 100)
    private String activityRegion; // 가이드 활동 지역

    @Column(nullable = false)
    private Boolean isDeleted = false; // 삭제 여부 (기본값: false)

    @Column(length = 255)
    private String introduction; // 가이드 소개


}

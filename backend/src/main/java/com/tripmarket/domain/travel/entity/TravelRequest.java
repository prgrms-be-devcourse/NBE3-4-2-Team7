package com.tripmarket.domain.travel.entity;

import com.tripmarket.domain.guide.entity.Guide;
import com.tripmarket.domain.member.entity.Member;
import com.tripmarket.global.jpa.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class TravelRequest extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Member user; // 요청 사용자 (Member와 연관 관계)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guide_id", nullable = false)
    private Guide guide; // 요청 가이드 (Guide와 연관 관계)

    @Column(length = 50, nullable = false)
    private String city; // 여행지 (도시 이름)

    @Column(nullable = false)
    private String startDate; // 여행 시작 날짜

    @Column(nullable = false)
    private String endDate; // 여행 종료 날짜

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TravelRequestStatus status; // 여행 요청 상태 (PENDING, ACCEPTED, REJECTED)

    @Column(nullable = false)
    private boolean isDeleted; // 삭제 여부 (true면 삭제된 상태)

    public enum TravelRequestStatus {
        PENDING, ACCEPTED, REJECTED // 여행 요청 상태 Enum
    }
}

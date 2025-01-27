package com.tripmarket.domain.travel.entity;

import com.tripmarket.global.jpa.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class TravelRequestHistory extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private TravelRequest travelRequest; // 관련된 여행 요청 (TravelRequest와 연관 관계)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TravelRequest.TravelRequestStatus status; // 변경된 상태 (TravelRequest.TravelRequestStatus Enum 사용)

    @Column(nullable = false)
    private String description; // 상태 변경에 대한 상세 메시지
}

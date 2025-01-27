package com.tripmarket.domain.travel.entity;

import com.tripmarket.global.jpa.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Getter
@NoArgsConstructor
@Entity
public class TravelCategory extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    @NonNull
    private TravelRequest travelRequest; // 관련된 여행 요청

    @Column(nullable = false, length = 50)
    private String name; // 카테고리 이름 (예: "자연", "역사")
}

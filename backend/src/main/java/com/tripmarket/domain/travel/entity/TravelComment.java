package com.tripmarket.domain.travel.entity;

import com.tripmarket.global.jpa.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
public class TravelComment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    @NonNull // null 값을 허용하지 않음
    private TravelRequest travelRequest;

    @Column(nullable = false, columnDefinition = "TEXT")
    @NonNull // null 값을 허용하지 않음
    private String comment;
}

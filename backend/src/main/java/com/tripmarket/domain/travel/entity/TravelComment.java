package com.tripmarket.domain.travel.entity;

import com.tripmarket.global.jpa.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;


@Getter
@NoArgsConstructor
@Entity
public class TravelComment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    @NonNull
    private Travel travel; // 여행 요청과의 관계

    @Column(nullable = false, columnDefinition = "TEXT")
    @NonNull
    private String comment; // 댓글 내용

}


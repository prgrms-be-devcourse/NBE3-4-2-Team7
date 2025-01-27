package com.tripmarket.domain.review.entity;


import com.tripmarket.global.jpa.entity.BaseEntity;
import com.tripmarket.domain.member.entity.Member;
import com.tripmarket.domain.guide.entity.Guide;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * Review 엔티티
 * - 사용자(Member)가 가이드(Guide)에 대해 작성한 리뷰를 저장
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
public class Review extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Member member; // 리뷰를 작성한 사용자(Member)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guide_id", nullable = false)
    private Guide guide; // 리뷰 대상이 되는 가이드(Guide)

    @Column(nullable = false, columnDefinition = "TEXT")
    private String comment; // 리뷰 내용 (텍스트 형식)

    @Column(nullable = false)
    private Integer reviewScore; // 리뷰 점수 (정수형, 예: 1 ~ 5)

    @Column(nullable = false)
    private Boolean isDeleted = false; // 삭제 여부 (true일 경우 삭제된 리뷰로 간주)

    /*
      Review 클래스 설명
      - 사용자와 가이드 간의 리뷰 정보를 저장하고 관리합니다.
      - isDeleted 필드를 통해 논리적 삭제를 처리합니다.
      - 연관 관계:
        - Member (리뷰를 작성한 사용자)
        - Guide (리뷰 대상 가이드)
     */
}
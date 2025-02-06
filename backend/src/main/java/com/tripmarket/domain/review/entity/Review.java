package com.tripmarket.domain.review.entity;

import com.tripmarket.domain.guide.entity.Guide;
import com.tripmarket.domain.member.entity.Member;
import com.tripmarket.global.jpa.entity.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "review") // 테이블 이름 명시
public class Review extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "guide_id", nullable = false)
	@Schema(description = "리뷰 대상 가이드 ID", example = "1")
	private Guide guide;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	@Schema(description = "리뷰 작성자 ID", example = "10")
	private Member member;

	@Column(nullable = false, columnDefinition = "TEXT")
	@Schema(description = "리뷰 내용", example = "가이드가 친절하고 유익했어요!")
	private String comment;

	//@Column(nullable = false, precision = 2, scale = 1)
	@Schema(description = "리뷰 점수 (0.0~5.0)", example = "3.3")
	private Double reviewScore; // 소수점 1자리까지 저장

	@Column(nullable = false)
	@Schema(description = "삭제 여부 (true = 삭제됨)", example = "false")
	private Boolean isDeleted = false;

	// 생성자
	public Review(Guide guide, Member member, String comment, double reviewScore) {
		this.guide = guide;
		this.member = member;
		this.comment = comment;
		this.reviewScore = reviewScore;
	}
}
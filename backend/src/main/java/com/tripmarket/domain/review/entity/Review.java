package com.tripmarket.domain.review.entity;

import com.tripmarket.domain.member.entity.Member;
import com.tripmarket.domain.travel.entity.Travel;
import com.tripmarket.global.jpa.entity.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "REVIEW")
public class Review extends BaseEntity {

	//아이디
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	@Schema(description = "리뷰 작성자 ID", example = "10")
	private Member member;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "travel_id", nullable = false)
	@Schema(description = "리뷰 대상 여행 ID", example = "100")
	private Travel travel;

	@Getter
	@Schema(description = "리뷰 대상 가이드 ID", example = "5")
	private Long guideId;

	@Size(max = 500, message = "리뷰 내용은 최대 500자까지 입력할 수 있습니다.")
	@Column(nullable = false, length = 500)
	@Schema(description = "리뷰 내용", example = "가이드가 친절하고 유익했어요!")
	private String comment;

	@Column(nullable = false)
	@Schema(description = "리뷰 점수 (0.0~5.0)", example = "3.3")
	private Double reviewScore;

	@Column(nullable = false)
	@Schema(description = "삭제 여부 (true = 삭제됨)", example = "false")
	private Boolean isDeleted = false;

	// 생성자
	@Builder
	public Review(Member member, Travel travel, String comment, Double reviewScore, Long guideId) {
		this.member = member;
		this.travel = travel;
		this.comment = comment;
		this.reviewScore = reviewScore;
		this.guideId = guideId; // 가이드 ID 저장
	}

	// 리뷰 내용 및 점수 수정
	public void update(String comment, double reviewScore) {
		this.comment = comment;
		this.reviewScore = reviewScore;
		this.updateTimestamp();
	}

	// 소프트 삭제 처리
	public void softDelete() {
		this.isDeleted = true;
	}

	// 삭제된 리뷰인지 확인
	public boolean isDeleted() {
		return isDeleted;
	}
}
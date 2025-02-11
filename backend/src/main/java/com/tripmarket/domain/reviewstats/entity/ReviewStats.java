package com.tripmarket.domain.reviewstats.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "review_stats")
@NoArgsConstructor
public class ReviewStats {

	@Id
	private Long guideId;

	@Column(nullable = false)
	private Long reviewCount = 0L;  // 리뷰 개수

	@Column(nullable = false)
	private double totalScore = 0.0;  // 모든 리뷰 별점의 합

	@Column(nullable = false)
	private double averageRating = 0.0;

	@Builder
	public ReviewStats(Long guideId, Long reviewCount, double totalScore) {
		this.guideId = guideId;
		this.reviewCount = reviewCount;
		this.totalScore = totalScore;
	}

	// 리뷰 추가 시 호출
	public void addReview(double reviewScore) {
		this.totalScore += reviewScore;
		this.reviewCount++;
		this.averageRating = this.reviewCount == 0 ? 0.0 : this.totalScore / this.reviewCount;
	}

	// 리뷰 삭제 시 호출
	public void removeReview(double reviewScore) {
		if (reviewCount > 0) {
			this.totalScore -= reviewScore;
			this.reviewCount--;
			this.averageRating = this.reviewCount == 0 ? 0.0 : this.totalScore / this.reviewCount;
		}
	}

	// 리뷰 수정 시 호출
	public void updateReviewScore(double oldScore, double newScore) {
		this.totalScore = this.totalScore - oldScore + newScore;
		this.averageRating = this.reviewCount == 0 ? 0.0 : this.totalScore / this.reviewCount;
	}

}
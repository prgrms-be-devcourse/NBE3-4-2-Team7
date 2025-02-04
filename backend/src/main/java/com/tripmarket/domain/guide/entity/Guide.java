package com.tripmarket.domain.guide.entity;

import java.util.List;

import com.tripmarket.domain.review.entity.Review;
import com.tripmarket.domain.travel.entity.TravelRequest;
import com.tripmarket.global.jpa.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Guide extends BaseEntity {

	@Column(nullable = false, length = 30)
	@Size(min = 1, max = 30)
	private String name;

	@Column(nullable = false, length = 100)
	@Size(min = 1, max = 100)
	private String activityRegion;

	@Column(nullable = false, length = 300)
	@Size(min = 1, max = 300)
	private String introduction;

	@Column(nullable = false)
	@ValidLanguages
	private String languages;

	@Min(0)
	@Max(100)
	private Integer experienceYears;

	@Column(nullable = false)
	@Builder.Default
	private boolean isDeleted = false;

	// TODO : Member 연관관계 해소
	// 가이드 프로필이 존재하는 유저

	// 가이드의 리뷰 리스트
	@OneToMany(mappedBy = "guide")
	List<Review> reviews;

	// 여행 요청 리스트
	@OneToMany(mappedBy = "guide")
	List<TravelRequest> travelRequests;

	// 리뷰 통계 테이블
	// @OneToOne
	// GuideReviewStats guideReviewStats;
}

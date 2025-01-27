package com.tripmarket.domain.guide.entity;

import java.util.List;

import com.tripmarket.domain.guide.dto.GuideDto;
import com.tripmarket.domain.review.entity.Review;
import com.tripmarket.domain.travel.entity.TravelRequest;
import com.tripmarket.global.jpa.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Max;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Guide extends BaseEntity {

	@Column(nullable = false, length = 10)
	private String name;

	@Column(length = 100)
	private String activityRegion;

	@Column(length = 300)
	private String introduction;

	@Column(length = 100)
	private String languages;

	@Max(100)
	private Integer experienceYears;

	@Column(nullable = false)
	// 삭제 여부, 기본값 : false
	private boolean isDeleted = false;

	// TODO : Member 연관관계 해소
	// 가이드 프로필이 존재하는 유저

	@OneToMany(mappedBy = "guide")
	// 가이드의 리뷰 리스트
	List<Review> reviews;

	// 여행 요청 리스트
	@OneToMany(mappedBy = "guide")
	List<TravelRequest> travelRequests;

	// DTO → Entity 변환
	public static Guide toEntity(GuideDto guideDto) {
		return Guide.builder()
			.name(guideDto.getName())
			.languages(guideDto.getLanguages())
			.introduction(guideDto.getIntroduction())
			.activityRegion(guideDto.getActivityRegion())
			.experienceYears(guideDto.getExperienceYears())
			.isDeleted(guideDto.isDeleted())
			.build();
	}
}

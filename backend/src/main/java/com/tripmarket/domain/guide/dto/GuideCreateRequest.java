package com.tripmarket.domain.guide.dto;

import com.tripmarket.domain.guide.entity.Guide;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class GuideCreateRequest {

	@NotBlank
	private String name;
	/*
	 * ISO 639 언어코드 형식의 문자열(소문자)
	 * EX) "kr, en"
	 * */
	@NotBlank
	private String languages;

	@NotBlank
	private String activityRegion;

	private Integer experienceYears;

	@NotBlank
	private String introduction;

	// DTO → Entity 변환
	public static Guide toEntity(GuideCreateRequest guideCreateRequest) {
		return Guide.builder()
			.name(guideCreateRequest.getName())
			.introduction(guideCreateRequest.getIntroduction())
			.activityRegion(guideCreateRequest.getActivityRegion())
			.experienceYears(guideCreateRequest.getExperienceYears())
			.languages(guideCreateRequest.getLanguages())
			.isDeleted(false)
			.build();
	}
}

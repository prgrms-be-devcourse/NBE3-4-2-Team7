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
public class GuideDto {
	private Long id;

	@NotBlank
	private String name;

	private Long userId;

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

	private boolean isDeleted;

	public void setDeleted(boolean deleted) {
		isDeleted = deleted;
	}

	// Entity -> Dto 변환
	public static GuideDto fromEntity(Guide guide) {
		return GuideDto.builder()
			.id(guide.getId())
			.name(guide.getName())
			.languages(guide.getLanguages())
			.activityRegion(guide.getActivityRegion())
			.experienceYears(guide.getExperienceYears())
			.introduction(guide.getIntroduction())
			.build();
	}

	// DTO → Entity 변환
	public static Guide toEntity(GuideDto guideDto) {
		return Guide.builder()
			.name(guideDto.getName())
			.introduction(guideDto.getIntroduction())
			.activityRegion(guideDto.getActivityRegion())
			.experienceYears(guideDto.getExperienceYears())
			.languages(guideDto.getLanguages())
			.isDeleted(guideDto.isDeleted())
			.build();
	}
}

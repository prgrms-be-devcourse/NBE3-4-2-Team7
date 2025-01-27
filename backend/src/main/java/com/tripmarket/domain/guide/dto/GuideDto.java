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

	// 가이드가 유저 프로필도 존재할 때
	private Long userId;

	@NotBlank
	private String languages;

	@NotBlank
	private String activityRegion;

	private Integer experienceYears;

	@NotBlank
	private String introduction;

	private boolean isDeleted;

	public static GuideDto of(Guide guide) {
		return GuideDto.builder()
			.id(guide.getId())
			.name(guide.getName())
			.languages(guide.getLanguages())
			.activityRegion(guide.getActivityRegion())
			.experienceYears(guide.getExperienceYears())
			.introduction(guide.getIntroduction())
			.build();
	}
}

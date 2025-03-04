package com.tripmarket.domain.guide.dto;

import com.tripmarket.domain.guide.entity.Guide;
import com.tripmarket.domain.guide.entity.ValidLanguages;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record GuideDto(
	Long id,
	@NotBlank String name,
	@NotBlank @ValidLanguages String languages,
	@NotBlank String activityRegion,
	Integer experienceYears,
	@NotBlank String introduction,
	boolean isDeleted
) {
	// Entity -> Record 변환
	public static GuideDto fromEntity(Guide guide) {
		return new GuideDto(
			guide.getId(),
			guide.getName(),
			guide.getLanguages(),
			guide.getActivityRegion(),
			guide.getExperienceYears(),
			guide.getIntroduction(),
			false
		);
	}

	// Record -> Entity 변환
	public static Guide toEntity(GuideDto guideDto) {
		return Guide.builder()
			.name(guideDto.name)
			.introduction(guideDto.introduction)
			.activityRegion(guideDto.activityRegion)
			.experienceYears(guideDto.experienceYears)
			.languages(guideDto.languages)
			.isDeleted(guideDto.isDeleted)
			.build();
	}
}

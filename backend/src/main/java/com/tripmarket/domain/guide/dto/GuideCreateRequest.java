package com.tripmarket.domain.guide.dto;

import com.tripmarket.domain.guide.entity.Guide;
import com.tripmarket.domain.guide.entity.ValidLanguages;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record GuideCreateRequest(
	@NotBlank String name,
	@NotBlank @ValidLanguages String languages,
	@NotBlank String activityRegion,
	@Max(100) @Min(0) Integer experienceYears,
	@NotBlank String introduction
) {

	// Record -> Entity 변환
	public static Guide toEntity(GuideCreateRequest guideCreateRequest) {
		return Guide.builder()
			.name(guideCreateRequest.name)
			.introduction(guideCreateRequest.introduction)
			.activityRegion(guideCreateRequest.activityRegion)
			.experienceYears(guideCreateRequest.experienceYears)
			.languages(guideCreateRequest.languages)
			.isDeleted(false)
			.build();
	}

}

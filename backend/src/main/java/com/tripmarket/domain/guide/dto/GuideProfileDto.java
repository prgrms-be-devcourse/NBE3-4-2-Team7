package com.tripmarket.domain.guide.dto;

import java.util.List;

import com.tripmarket.domain.guide.entity.Guide;
import com.tripmarket.domain.review.dto.ReviewResponseDto;
import com.tripmarket.domain.review.entity.Review;
import com.tripmarket.domain.reviewstats.entity.ReviewStats;
import com.tripmarket.domain.review.dto.ReviewResponseDto;

public record GuideProfileDto(
	Long id,
	String name,
	String languages,
	String activityRegion,
	Integer experienceYears,
	String introduction,
	Long reviewCount,
	double averageRating,
	List<ReviewResponseDto>reviews
) {
	public static GuideProfileDto fromEntity(Guide guide, ReviewStats reviewStats, List<Review> reviews)  {
		return new GuideProfileDto(
			guide.getId(),
			guide.getName(),
			guide.getLanguages(),
			guide.getActivityRegion(),
			guide.getExperienceYears(),
			guide.getIntroduction(),
			reviewStats != null ? reviewStats.getReviewCount() : 0L,
			reviewStats != null ? reviewStats.getAverageRating() : 0.0,
			reviews.stream().map(ReviewResponseDto::fromEntity).toList()
		);
	}
}

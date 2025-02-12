package com.tripmarket.domain.reviewstats.repository;

import com.tripmarket.domain.reviewstats.entity.ReviewStats;

public interface ReviewStatsRepositoryCustom {
	ReviewStats getReviewStatsByGuideId(Long guideId);
}

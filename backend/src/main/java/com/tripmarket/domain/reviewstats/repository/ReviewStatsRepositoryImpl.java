package com.tripmarket.domain.reviewstats.repository;

import org.springframework.stereotype.Repository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tripmarket.domain.reviewstats.entity.ReviewStats;
import com.tripmarket.domain.reviewstats.entity.QReviewStats;

import lombok.RequiredArgsConstructor;
@Repository
@RequiredArgsConstructor
public class ReviewStatsRepositoryImpl implements ReviewStatsRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public ReviewStats getReviewStatsByGuideId(Long guideId) {
		QReviewStats reviewStats = QReviewStats.reviewStats;

		return queryFactory
			.selectFrom(reviewStats)
			.where(reviewStats.guideId.eq(guideId))
			.fetchOne();
	}

}

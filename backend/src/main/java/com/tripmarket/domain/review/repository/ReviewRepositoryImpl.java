package com.tripmarket.domain.review.repository;

import java.util.List;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tripmarket.domain.review.entity.QReview;
import com.tripmarket.domain.review.entity.Review;
import com.tripmarket.domain.travel.entity.Travel;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<Review> findReviewsByTravel(Travel travel) {
		return queryFactory
			.selectFrom(QReview.review)
			.where(QReview.review.travel.eq(travel)
				.and(QReview.review.isDeleted.isFalse()))
			.fetch();
	}

	@Override
	public List<Review> findReviewsByTravelList(List<Long> travelIds) {
		return queryFactory
			.selectFrom(QReview.review)
			.where(QReview.review.travel.id.in(travelIds)
				.and(QReview.review.isDeleted.isFalse()))
			.fetch();
	}

	@Override
	public boolean existsReviewByMemberAndTravel(Long memberId, Long travelId) {
		Integer count = queryFactory
			.selectOne()
			.from(QReview.review)
			.where(QReview.review.member.id.eq(memberId)
				.and(QReview.review.travel.id.eq(travelId))
				.and(QReview.review.isDeleted.isFalse()))
			.fetchFirst();

		return count != null;
	}
}

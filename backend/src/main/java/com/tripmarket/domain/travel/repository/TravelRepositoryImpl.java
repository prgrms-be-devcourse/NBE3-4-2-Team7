package com.tripmarket.domain.travel.repository;

import static com.tripmarket.domain.travel.entity.QTravel.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tripmarket.domain.travel.entity.Travel;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TravelRepositoryImpl implements TravelRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<Travel> searchTravels(Long categoryId, Pageable pageable) {
		List<Travel> travels = queryFactory
			.selectFrom(travel)
			.where(categoryId != null ? travel.category.id.eq(categoryId) : null)
			.orderBy(travel.createdAt.asc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		long total = queryFactory
			.selectFrom(travel)
			.where(categoryId != null ? travel.category.id.eq(categoryId) : null)
			.fetchCount();

		return new PageImpl<>(travels, pageable, total);
	}

	@Override
	public List<Travel> searchTravelsNoOffset(Long categoryId, LocalDateTime lastCreatedAt, int limit) {
		return queryFactory
			.selectFrom(travel)
			.where(
				categoryId != null ? travel.category.id.eq(categoryId) : null,
				lastCreatedAt != null ? travel.createdAt.gt(lastCreatedAt) : null
			)
			.orderBy(travel.createdAt.asc())
			.limit(limit)
			.fetch();
	}
}

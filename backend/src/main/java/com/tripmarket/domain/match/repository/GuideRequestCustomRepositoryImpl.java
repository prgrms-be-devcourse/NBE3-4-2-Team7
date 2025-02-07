package com.tripmarket.domain.match.repository;

// import static com.tripmarket.domain.QGuide.*;
// import static com.tripmarket.domain.QGuideRequest.*;
// import static com.tripmarket.domain.QMember.*;
// import static com.tripmarket.domain.QTravel.*;

import java.util.List;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tripmarket.domain.guide.entity.QGuide;
import com.tripmarket.domain.match.entity.GuideRequest;
import com.tripmarket.domain.match.entity.QGuideRequest;
import com.tripmarket.domain.member.entity.QMember;
import com.tripmarket.domain.travel.entity.QTravel;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GuideRequestCustomRepositoryImpl implements GuideRequestCustomRepository {

	private final JPAQueryFactory queryFactory;

	QGuideRequest guideRequest = QGuideRequest.guideRequest;
	QTravel travel = QTravel.travel;
	QGuide guide = QGuide.guide;
	QMember member = QMember.member;

	@Override
	public List<GuideRequest> findDetailedByMemberId(Long memberId) {
		return queryFactory
			.selectFrom(guideRequest)
			.join(guideRequest.travel, travel).fetchJoin()
			.join(guideRequest.guide, guide).fetchJoin()
			.join(guideRequest.member, member).fetchJoin()
			.where(guideRequest.member.id.eq(memberId))
			.fetch();
	}
}



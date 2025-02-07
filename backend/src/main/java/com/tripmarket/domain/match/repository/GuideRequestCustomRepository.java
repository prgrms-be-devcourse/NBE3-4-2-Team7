package com.tripmarket.domain.match.repository;

import java.util.List;

import com.tripmarket.domain.match.entity.GuideRequest;

public interface GuideRequestCustomRepository {
	List<GuideRequest> findDetailedByMemberId(Long memberId);
}


package com.tripmarket.domain.match.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tripmarket.domain.match.entity.GuideRequest;

@Repository
public interface GuideRequestRepository extends JpaRepository<GuideRequest, Long> {

	boolean existsByUserIdAndGuideIdAndTravelId(Long userId, Long guideId, Long travelId);
}

package com.tripmarket.domain.match.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tripmarket.domain.match.entity.GuideRequest;

@Repository
public interface GuideRequestRepository extends JpaRepository<GuideRequest, Long>, GuideRequestCustomRepository {

	boolean existsByMemberIdAndGuideIdAndTravelId(Long userId, Long guideId, Long travelId);

	List<GuideRequest> findByMemberId(Long userId);

	List<GuideRequest> findByGuideId(Long guideId);

}

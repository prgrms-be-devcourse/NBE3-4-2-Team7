package com.tripmarket.domain.match.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tripmarket.domain.match.entity.TravelOffer;

@Repository
public interface TravelOfferRepository extends JpaRepository<TravelOffer, Long> {
	boolean existsByGuideIdAndTravelId(Long guideId, Long travelId);
}

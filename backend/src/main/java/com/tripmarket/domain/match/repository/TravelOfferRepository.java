package com.tripmarket.domain.match.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tripmarket.domain.match.entity.TravelOffer;

@Repository
public interface TravelOfferRepository extends JpaRepository<TravelOffer, Long> {

	boolean existsByGuideIdAndTravelId(Long guideId, Long travelId);

	List<TravelOffer> findByGuideId(Long guideId);

	List<TravelOffer> findByTravelIdIn(List<Long> travelIds);

}

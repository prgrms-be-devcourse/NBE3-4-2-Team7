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

	// 특정 가이드가 연결된 여행 ID 목록 조회
	@Query("SELECT to.travel.id FROM TravelOffer to WHERE to.guide.id = :guideId")
	List<Long> findTravelIdsByGuideId(@Param("guideId") Long guideId);

	// 특정 여행에 대한 가이드 ID 조회 (여행이 어떤 가이드와 연결되어 있는지)
	@Query("SELECT to.guide.id FROM TravelOffer to WHERE to.travel.id = :travelId")
	List<Long> findGuideIdByTravelId(@Param("travelId") Long travelId);

	boolean existsByGuideIdAndTravelId(Long guideId, Long travelId);

}


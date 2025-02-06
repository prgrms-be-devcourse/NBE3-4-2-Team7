package com.tripmarket.domain.match.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tripmarket.domain.match.entity.GuideRequest;

@Repository
public interface GuideRequestRepository extends JpaRepository<GuideRequest, Long> {

	// 특정 가이드가 연결된 여행 ID 목록 조회
	@Query("SELECT gr.travel.id FROM GuideRequest gr WHERE gr.guide.id = :guideId")
	List<Long> findTravelIdsByGuideId(@Param("guideId") Long guideId);

	// 특정 여행에 대한 가이드 ID 조회 (여행이 어떤 가이드와 연결되어 있는지)
	@Query("SELECT gr.guide.id FROM GuideRequest gr WHERE gr.travel.id = :travelId")
	List<Long> findGuideIdByTravelId(@Param("travelId") Long travelId);

	// 특정 사용자가 특정 가이드와 특정 여행에 대해 매칭되어 있는지 확인
	boolean existsByUserIdAndGuideIdAndTravelId(Long userId, Long guideId, Long travelId);

	// 특정 사용자가 특정 여행을 수행했는지 확인 (리뷰 작성 가능 여부 체크)
	boolean existsByUserIdAndTravelId(Long userId, Long travelId);
}

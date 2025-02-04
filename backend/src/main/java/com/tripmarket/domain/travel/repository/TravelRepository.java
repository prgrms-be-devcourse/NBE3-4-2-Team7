package com.tripmarket.domain.travel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tripmarket.domain.guide.entity.Guide;
import com.tripmarket.domain.member.entity.Member;
import com.tripmarket.domain.travel.entity.Travel;

@Repository
public interface TravelRepository extends JpaRepository<Travel, Long>, TravelRepositoryCustom {

	default boolean hasMatchedTravel(Member user, Guide guide) {
		return existsByUserAndGuideAndStatus(user, guide, Travel.Status.MATCHED);
	}

	boolean existsByUserAndGuideAndStatus(Member user, Guide guide, Travel.Status status);
}

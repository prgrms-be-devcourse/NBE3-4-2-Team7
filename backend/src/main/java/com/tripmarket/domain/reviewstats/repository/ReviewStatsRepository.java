package com.tripmarket.domain.reviewstats.repository;

import com.tripmarket.domain.reviewstats.entity.ReviewStats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewStatsRepository extends JpaRepository<ReviewStats, Long>, ReviewStatsRepositoryCustom {
	Optional<ReviewStats> findByGuideId(Long guideId);
}

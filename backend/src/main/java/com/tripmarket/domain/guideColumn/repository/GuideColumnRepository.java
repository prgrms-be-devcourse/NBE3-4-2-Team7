package com.tripmarket.domain.guideColumn.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.tripmarket.domain.guideColumn.entity.GuideColumn;

public interface GuideColumnRepository extends JpaRepository<GuideColumn, Long> {
	Page<GuideColumn> findAllByOrderByCreatedAtDesc(Pageable pageable);

	List<GuideColumn> findByGuideIdOrderByCreatedAtDesc(long guideId);
}

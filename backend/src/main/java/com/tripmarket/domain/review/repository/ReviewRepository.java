package com.tripmarket.domain.review.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tripmarket.domain.review.entity.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
	List<Review> findByGuide_Id(Long guideId); // 특정 가이드의 리뷰 리스트 조회
}

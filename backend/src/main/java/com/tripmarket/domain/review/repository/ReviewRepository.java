package com.tripmarket.domain.review.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tripmarket.domain.member.entity.Member;
import com.tripmarket.domain.review.entity.Review;
import com.tripmarket.domain.travel.entity.Travel;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

	// 특정 여행 ID에 해당하는 리뷰 조회 (삭제되지 않은 리뷰만)
	List<Review> findByTravelAndIsDeletedFalse(Travel travel);

	// 특정 여행 리스트에 속한 리뷰 조회 (삭제되지 않은 리뷰만)
	List<Review> findByTravelInAndIsDeletedFalse(List<Travel> travels);

	// 특정 사용자가 특정 여행에 대해 리뷰를 작성했는지 확인 (중복 리뷰 방지)
	boolean existsByMemberAndTravelAndIsDeletedFalse(Member member, Travel travel);
}

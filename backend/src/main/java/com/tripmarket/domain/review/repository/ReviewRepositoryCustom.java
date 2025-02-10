package com.tripmarket.domain.review.repository;

import java.util.List;

import com.tripmarket.domain.review.entity.Review;
import com.tripmarket.domain.travel.entity.Travel;

public interface ReviewRepositoryCustom {
	// 특정 여행 ID에 해당하는 리뷰 조회
	List<Review> findReviewsByTravel(Travel travel);
	// 특정 여행 리스트에 속한 리뷰 조회
	List<Review> findReviewsByTravelList(List<Long> travelIds);
	// 특정 사용자가 특정 여행에 대해 리뷰를 작성했는지 확인
	boolean existsReviewByMemberAndTravel(Long memberId, Long travelId);

}

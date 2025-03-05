package com.tripmarket.domain.match.dto;

import com.tripmarket.domain.match.entity.GuideRequest;

public record GuideRequestDto(
	Long id,
	Long travelId,
	String travelCity,
	boolean isTravelDeleted,
	Long guideId,
	String guideName,
	boolean isGuideDeleted,
	String memberName,
	String status,
	String userEmail,  // 사용자 이메일 추가
	String guideEmail  // 가이드 이메일 추가
) {
	public static GuideRequestDto of(GuideRequest guideRequest) {
		return new GuideRequestDto(
			guideRequest.getId(),
			guideRequest.getTravel().getId(),
			guideRequest.getTravel().getCity(),
			guideRequest.getTravel().isDeleted(),
			guideRequest.getGuide().getId(),
			guideRequest.getGuide().getName(),
			guideRequest.getGuide().isDeleted(),
			guideRequest.getMember().getName(),
			guideRequest.getStatus().toString(),
			guideRequest.getGuide().getMember().getEmail(),
			guideRequest.getMember().getEmail() // 사용자 이메일
		);
	}

	public static GuideRequestDto response(GuideRequest guideRequest,String email) {
		return new GuideRequestDto(
			guideRequest.getId(),
			guideRequest.getTravel().getId(),
			guideRequest.getTravel().getCity(),
			guideRequest.getTravel().isDeleted(),
			guideRequest.getGuide().getId(),
			guideRequest.getGuide().getName(),
			guideRequest.getGuide().isDeleted(),
			guideRequest.getMember().getName(),
			guideRequest.getStatus().toString(),
			email, // 가이드 이메일
			guideRequest.getMember().getEmail() // 사용자 이메일
		);
	}
}

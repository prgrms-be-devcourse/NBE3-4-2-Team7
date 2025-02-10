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
	String status
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
			guideRequest.getStatus().toString()
		);
	}
}

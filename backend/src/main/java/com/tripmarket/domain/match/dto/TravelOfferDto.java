package com.tripmarket.domain.match.dto;

import com.tripmarket.domain.match.entity.TravelOffer;

public record TravelOfferDto(
	Long id,
	Long travelId,
	String travelCity,
	boolean isTravelDeleted,
	Long guideId,
	String guideName,
	boolean isGuideDeleted,
	String status
) {
	public static TravelOfferDto of(TravelOffer travelOffer) {
		return new TravelOfferDto(
			travelOffer.getId(),
			travelOffer.getTravel().getId(),
			travelOffer.getTravel().getCity(),
			travelOffer.getTravel().isDeleted(),
			travelOffer.getGuide().getId(),
			travelOffer.getGuide().getName(),
			travelOffer.getGuide().isDeleted(),
			travelOffer.getStatus().toString()
		);
	}
}

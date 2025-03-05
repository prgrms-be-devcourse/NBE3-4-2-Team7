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
	String status,
	String guideEmail,    // 가이드 이메일 추가
	String userEmail
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
			travelOffer.getStatus().toString(),
			travelOffer.getGuide().getMember().getEmail(), // 가이드 이메일
			travelOffer.getTravel().getUser().getEmail()// 사용자 이메일
		);
	}

	public static TravelOfferDto response(TravelOffer travelOffer,String email) {
		return new TravelOfferDto(
			travelOffer.getId(),
			travelOffer.getTravel().getId(),
			travelOffer.getTravel().getCity(),
			travelOffer.getTravel().isDeleted(),
			travelOffer.getGuide().getId(),
			travelOffer.getGuide().getName(),
			travelOffer.getGuide().isDeleted(),
			travelOffer.getStatus().toString(),
			email, // 가이드 이메일
			travelOffer.getTravel().getUser().getEmail()// 사용자 이메일
		);
	}
}

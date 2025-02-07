package com.tripmarket.domain.match.dto;

import com.tripmarket.domain.match.entity.TravelOffer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TravelOfferDto {
	private Long id;
	private Long travelId;
	private String travelCity; // 여행 제목
	private boolean isTravelDeleted; // 여행 삭제 여부
	private Long guideId;
	private String guideName; // 가이드 이름
	private boolean isGuideDeleted; // 가이드 삭제 여부
	private String status;

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


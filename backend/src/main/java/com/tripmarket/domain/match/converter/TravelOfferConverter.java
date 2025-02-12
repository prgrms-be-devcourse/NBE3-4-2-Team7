package com.tripmarket.domain.match.converter;

import org.springframework.stereotype.Component;

import com.tripmarket.domain.guide.entity.Guide;
import com.tripmarket.domain.match.entity.TravelOffer;
import com.tripmarket.domain.match.enums.MatchRequestStatus;
import com.tripmarket.domain.travel.entity.Travel;

@Component
public class TravelOfferConverter {

	public TravelOffer toEntity(Guide guide, Travel travel) {
		return TravelOffer.builder()
			.guide(guide)
			.travel(travel)
			.status(MatchRequestStatus.PENDING)
			.build();
	}
}

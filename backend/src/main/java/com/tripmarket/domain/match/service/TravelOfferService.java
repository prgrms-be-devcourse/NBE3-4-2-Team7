package com.tripmarket.domain.match.service;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tripmarket.domain.guide.entity.Guide;
import com.tripmarket.domain.match.converter.TravelOfferConverter;
import com.tripmarket.domain.match.entity.TravelOffer;
import com.tripmarket.domain.match.enums.MatchRequestStatus;
import com.tripmarket.domain.match.repository.TravelOfferRepository;
import com.tripmarket.domain.member.entity.Member;
import com.tripmarket.domain.member.service.MemberService;
import com.tripmarket.domain.travel.entity.Travel;
import com.tripmarket.domain.travel.enums.TravelStatus;
import com.tripmarket.domain.travel.service.TravelService;
import com.tripmarket.global.exception.CustomException;
import com.tripmarket.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TravelOfferService {

	private final TravelService travelService;
	private final MemberService memberService;
	private final TravelOfferConverter travelOfferConverter;
	private final TravelOfferRepository travelOfferRepository;

	@Transactional
	public void createTravelOffer(String email, Long travelId) {
		Member member = memberService.getMemberByEmail(email);
		Guide guide = member.getGuide();
		Travel travel = travelService.getTravel(travelId);
		validateSelfResponse(guide, travel);
		validateDuplicateTravelOffer(guide, travelId);

		travel.updateTravelStatus(TravelStatus.IN_PROGRESS);

		TravelOffer travelOffer = travelOfferConverter.toEntity(guide, travel);
		travelOfferRepository.save(travelOffer);
	}

	@Transactional
	public void matchTravelOffer(Long requestId, String email, MatchRequestStatus status) {
		Member member = memberService.getMemberByEmail(email);
		TravelOffer travelOffer = getTravelOffer(requestId);
		validateTravelOfferOwnership(travelOffer, member);

		travelOffer.updateStatus(status);
		travelOfferRepository.save(travelOffer);
	}

	public void validateSelfResponse(Guide guide, Travel travel) {
		Long travelOwnerId = travel.getUser().getId();
		Long guideOwnerId = guide.getMember().getId();

		if (Objects.equals(travelOwnerId, guideOwnerId)) {
			throw new CustomException(ErrorCode.SELF_RESPONSE_NOT_ALLOWED);
		}
	}

	public void validateDuplicateTravelOffer(Guide guide, Long travelId) {
		boolean exists = travelOfferRepository.existsByGuideIdAndTravelId(guide.getId(), travelId);
		if (exists) {
			throw new CustomException(ErrorCode.DUPLICATE_TRAVEL_OFFER);
		}
	}

	public void validateTravelOfferOwnership(TravelOffer travelOffer, Member member) {
		Travel travel = travelOffer.getTravel();
		Long travelOwnerId = travel.getUser().getId();
		Long requesterId = member.getId();

		if (!Objects.equals(travelOwnerId, requesterId)) {
			throw new CustomException(ErrorCode.MEMBER_ACCESS_DENIED);
		}
	}

	public TravelOffer getTravelOffer(Long requestId) {
		return travelOfferRepository.findById(requestId)
			.orElseThrow(() -> new CustomException(ErrorCode.TRAVEL_OFFER_NOT_FOUND));
	}
}

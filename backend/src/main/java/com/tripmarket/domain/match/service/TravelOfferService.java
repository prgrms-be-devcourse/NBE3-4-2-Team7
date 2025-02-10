package com.tripmarket.domain.match.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tripmarket.domain.guide.dto.GuideDto;
import com.tripmarket.domain.guide.entity.Guide;
import com.tripmarket.domain.guide.service.GuideService;
import com.tripmarket.domain.match.entity.TravelOffer;
import com.tripmarket.domain.match.repository.TravelOfferRepository;
import com.tripmarket.domain.member.entity.Member;
import com.tripmarket.domain.member.service.MemberService;
import com.tripmarket.domain.travel.entity.Travel;
import com.tripmarket.domain.travel.service.TravelService;
import com.tripmarket.global.exception.CustomException;
import com.tripmarket.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TravelOfferService {

	private final GuideService guideService;
	private final TravelService travelService;
	private final TravelOfferRepository travelOfferRepository;
	private final MemberService memberService;

	@Transactional
	public void createTravelOffer(String email, Long travelId) {
		Member member = memberService.getMemberByEmail(email);
		Guide guide = GuideDto.toEntity(guideService.getGuideByMember(member.getId()));
		Travel travel = travelService.getTravel(travelId);
		validateSelfResponse(guide, travel);
		validateDuplicateTravelOffer(guide, travelId);

		travel.updateTravelStatus(Travel.Status.IN_PROGRESS);

		TravelOffer matchRequest = TravelOffer.builder()
			.guide(guide)
			.travel(travel)
			.status(TravelOffer.RequestStatus.PENDING)
			.build();
		travelOfferRepository.save(matchRequest);
	}

	@Transactional
	public void matchTravelOffer(Long requestId, String email, TravelOffer.RequestStatus status) {
		Member member = memberService.getMemberByEmail(email);
		TravelOffer travelOffer = getTravelOffer(requestId);
		validateTravelOfferOwnership(travelOffer, member);

		travelOffer.updateStatus(status);
		travelOfferRepository.save(travelOffer);
	}

	public void validateSelfResponse(Guide guide, Travel travel) {
		if (travel.getUser().getId().equals(guide.getMember().getId())) {
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
		if (!travel.getUser().getId().equals(member.getId())) {
			throw new CustomException(ErrorCode.MEMBER_ACCESS_DENIED);
		}
	}

	public TravelOffer getTravelOffer(Long requestId) {
		return travelOfferRepository.findById(requestId)
			.orElseThrow(() -> new CustomException(ErrorCode.TRAVEL_OFFER_NOT_FOUND));
	}
}

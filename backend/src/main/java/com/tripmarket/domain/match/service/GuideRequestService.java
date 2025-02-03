package com.tripmarket.domain.match.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tripmarket.domain.guide.entity.Guide;
import com.tripmarket.domain.guide.service.GuideService;
import com.tripmarket.domain.match.dto.GuideRequestCreate;
import com.tripmarket.domain.match.entity.GuideRequest;
import com.tripmarket.domain.match.repository.GuideRequestRepository;
import com.tripmarket.domain.member.entity.Member;
import com.tripmarket.domain.member.service.MemberService;
import com.tripmarket.domain.travel.entity.Travel;
import com.tripmarket.domain.travel.service.TravelService;
import com.tripmarket.global.exception.CustomException;
import com.tripmarket.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GuideRequestService {

	private final MemberService memberService;
	private final GuideService guideService;
	private final TravelService travelService;
	private final GuideRequestRepository guideRequestRepository;

	@Transactional
	public void createGuideRequest(Long userId, Long guideId, GuideRequestCreate requestDto) {
		Member member = memberService.getMember(userId);
		Guide guide = guideService.getGuide(guideId);
		validateSelfRequest(userId, guide);
		validateDuplicateRequest(userId, guideId, requestDto.getTravelId());

		Travel travel = travelService.getTravel(requestDto.getTravelId());
		travelService.validateOwnership(userId, travel);
		travel.updateTravelStatus(Travel.Status.IN_PROGRESS);

		GuideRequest guideRequest = requestDto.toEntity(member, guide, travel);
		guideRequestRepository.save(guideRequest);
	}

	@Transactional
	public void matchGuideRequest(Long travelRequestId, Long guideId, GuideRequest.RequestStatus status) {
		GuideRequest guideRequest = getGuideRequest(travelRequestId);
		validateGuideOwnership(guideId, guideRequest);

		guideRequest.updateStatus(status);
		guideRequestRepository.save(guideRequest);
	}

	public void validateDuplicateRequest(Long userId, Long guideId, Long travelId) {
		boolean alreadyRequested = guideRequestRepository.existsByUserIdAndGuideIdAndTravelId(userId, guideId,
			travelId);
		if (alreadyRequested) {
			throw new CustomException(ErrorCode.DUPLICATE_REQUEST);
		}
	}

	public void validateSelfRequest(Long userId, Guide guide) {
		if (userId.equals(guide.getMember().getId())) {
			throw new CustomException(ErrorCode.SELF_REQUEST_NOT_ALLOWED);
		}
	}

	public GuideRequest getGuideRequest(Long travelRequestId) {
		return guideRequestRepository.findById(travelRequestId)
			.orElseThrow(() -> new CustomException(ErrorCode.GUIDE_REQUEST_NOT_FOUND));
	}

	public void validateGuideOwnership(Long guideId, GuideRequest guideRequest) {
		if (!guideRequest.getGuide().getId().equals(guideId)) {
			throw new CustomException(ErrorCode.GUIDE_ACCESS_DENIED);
		}
	}
}

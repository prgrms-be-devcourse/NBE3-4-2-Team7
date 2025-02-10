package com.tripmarket.domain.match.service;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tripmarket.domain.guide.entity.Guide;
import com.tripmarket.domain.guide.service.GuideService;
import com.tripmarket.domain.match.dto.request.GuideRequestCreate;
import com.tripmarket.domain.match.entity.GuideRequest;
import com.tripmarket.domain.match.enums.MatchRequestStatus;
import com.tripmarket.domain.match.repository.GuideRequestRepository;
import com.tripmarket.domain.member.entity.Member;
import com.tripmarket.domain.member.repository.MemberRepository;
import com.tripmarket.domain.travel.entity.Travel;
import com.tripmarket.domain.travel.enums.TravelStatus;
import com.tripmarket.domain.travel.service.TravelService;
import com.tripmarket.global.exception.CustomException;
import com.tripmarket.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GuideRequestService {

	private final MemberRepository memberRepository;
	private final GuideService guideService;
	private final TravelService travelService;
	private final GuideRequestRepository guideRequestRepository;

	@Transactional
	public void createGuideRequest(String email, Long guideId, GuideRequestCreate requestDto) {
		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

		Guide guide = guideService.getGuide(guideId);
		validateSelfRequest(member, guide);
		validateDuplicateRequest(member.getId(), guideId, requestDto.travelId());

		Travel travel = travelService.getTravel(requestDto.travelId());
		travelService.validateOwnership(member, travel);
		travel.updateTravelStatus(TravelStatus.IN_PROGRESS);

		GuideRequest guideRequest = requestDto.toEntity(member, guide, travel);
		guideRequestRepository.save(guideRequest);
	}

	@Transactional
	public void matchGuideRequest(Long travelRequestId, Long guideId, MatchRequestStatus status) {
		GuideRequest guideRequest = getGuideRequest(travelRequestId);
		validateGuideOwnership(guideId, guideRequest);

		guideRequest.updateStatus(status);
		guideRequestRepository.save(guideRequest);
	}

	public void validateDuplicateRequest(Long userId, Long guideId, Long travelId) {
		// 가이드 프로필이 없는 유저가 요청할 경우
		if (userId == null) {
			return;
		}

		boolean alreadyRequested = guideRequestRepository.existsByMemberIdAndGuideIdAndTravelId(userId, guideId,
			travelId);
		if (alreadyRequested) {
			throw new CustomException(ErrorCode.DUPLICATE_REQUEST);
		}
	}

	public void validateSelfRequest(Member member, Guide guide) {
		Long guideOwnerId = guide.getMember().getId();
		Long requesterId = member.getId();

		if (Objects.equals(requesterId, guideOwnerId)) {
			throw new CustomException(ErrorCode.SELF_REQUEST_NOT_ALLOWED);
		}
	}

	public GuideRequest getGuideRequest(Long travelRequestId) {
		return guideRequestRepository.findById(travelRequestId)
			.orElseThrow(() -> new CustomException(ErrorCode.GUIDE_REQUEST_NOT_FOUND));
	}

	public void validateGuideOwnership(Long guideId, GuideRequest guideRequest) {
		Long requestGuideId = guideRequest.getGuide().getId();

		if (!Objects.equals(requestGuideId, guideId)) {
			throw new CustomException(ErrorCode.GUIDE_ACCESS_DENIED);
		}
	}
}

package com.tripmarket.domain.guide.service;

import org.springframework.stereotype.Service;

import com.tripmarket.domain.guide.entity.Guide;
import com.tripmarket.domain.guide.repository.GuideRepository;
import com.tripmarket.global.exception.CustomException;
import com.tripmarket.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GuideService {

	private final GuideRepository guideRepository;

	public Guide getGuide(Long guideId) {
		return guideRepository.findById(guideId)
			.orElseThrow(() -> new CustomException(ErrorCode.GUIDE_NOT_FOUND));
	}

	public Guide getGuideByMember(Long userId) {
		return guideRepository.findByMemberId(userId)
			.orElseThrow(() -> new CustomException(ErrorCode.GUIDE_PROFILE_NOT_FOUND));
	}
}

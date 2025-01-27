package com.tripmarket.domain.guide.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tripmarket.domain.guide.dto.GuideDto;
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

	public GuideDto findById(Long id) {
		// TODO : 익셉션 정의하기
		Guide guide = guideRepository.findById(id)
			.orElseThrow(() -> new RuntimeException("GUIDE NOT FOUND"));
		return GuideDto.of(guide);
	}

	@Transactional
	public void create(GuideDto guideDto) {
		guideRepository.save(Guide.toEntity(guideDto));
	}

	public Guide getGuideByMember(Long userId) {
		return guideRepository.findByMemberId(userId)
			.orElseThrow(() -> new CustomException(ErrorCode.GUIDE_PROFILE_NOT_FOUND));
	}

	@Transactional
	public void update(GuideDto guideDto) {
		guideRepository.save(Guide.toEntity(guideDto));
	}

	public List<GuideDto> findAll() {
		return guideRepository.findAll().stream()
			.map(GuideDto::of)
			.toList();
		}
}

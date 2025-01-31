package com.tripmarket.domain.guide.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tripmarket.domain.guide.dto.GuideDto;
import com.tripmarket.domain.guide.entity.Guide;
import com.tripmarket.domain.guide.repository.GuideRepository;
import com.tripmarket.domain.review.entity.Review;
import com.tripmarket.global.exception.EntityNotFoundException;

@Service
public class GuideService {
	private final GuideRepository guideRepository;
	private final GuideValidationService guideValidationService;

	@Autowired
	public GuideService(GuideRepository guideRepository, GuideValidationService guideValidationService) {
		this.guideRepository = guideRepository;
		this.guideValidationService = guideValidationService;
	}

	public GuideDto findById(Long id) {
		Guide guide = guideRepository
			.findById(id)
			.orElseThrow(() -> new EntityNotFoundException("GUIDE NOT FOUND"));
		return GuideDto.of(guide);
	}

	@Transactional
	public void create(GuideDto guideDto) {
		Guide guide = GuideDto.toEntity(guideDto);
		guideValidationService.checkValid(guide);
		guideRepository.save(guide);
	}

	@Transactional
	public void update(GuideDto guideDto) {
		Guide guide = GuideDto.toEntity(guideDto);
		guideValidationService.checkValid(guide);
		guideRepository.save(guide);
	}

	public List<GuideDto> getAllGuides() {
		return guideRepository.findAll().stream()
			.map(GuideDto::of)
			.toList();
	}

	public void delete(Long id) {
		// 가이드 가져와서 상태 업데이트
		GuideDto guideDto = findById(id);
		guideDto.setDeleted(true);
		guideRepository.save(GuideDto.toEntity(guideDto));
	}

	public List<Review> getAllReviews(Long id) {
		Guide guide = GuideDto.toEntity(findById(id));
		// TODO : review repository 에서 리뷰 전체 가져오기 ( 패치 사이즈 몇?)
		return List.of();
	}
}

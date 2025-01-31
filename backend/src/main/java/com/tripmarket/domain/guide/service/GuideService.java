package com.tripmarket.domain.guide.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tripmarket.domain.guide.dto.GuideDto;
import com.tripmarket.domain.guide.entity.Guide;
import com.tripmarket.domain.guide.repository.GuideRepository;
import com.tripmarket.domain.review.entity.Review;

@Service
public class GuideService {
	private final GuideRepository guideRepository;

	@Autowired
	public GuideService(GuideRepository guideRepository) {
		this.guideRepository = guideRepository;
	}

	public GuideDto findById(Long id) {
		// TODO : 익셉션 정의하기
		Guide guide = guideRepository.findById(id)
			.orElseThrow(() -> new RuntimeException("GUIDE NOT FOUND"));
		return GuideDto.of(guide);
	}

	@Transactional
	public void create(GuideDto guideDto) {
		guideRepository.save(GuideDto.toEntity(guideDto));
	}

	@Transactional
	public void update(GuideDto guideDto) {
		guideRepository.save(GuideDto.toEntity(guideDto));
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

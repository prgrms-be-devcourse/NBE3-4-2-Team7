package com.tripmarket.domain.guide.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tripmarket.domain.guide.dto.GuideDto;
import com.tripmarket.domain.guide.entity.Guide;
import com.tripmarket.domain.guide.repository.GuideRepository;

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
		guideRepository.save(Guide.toEntity(guideDto));
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

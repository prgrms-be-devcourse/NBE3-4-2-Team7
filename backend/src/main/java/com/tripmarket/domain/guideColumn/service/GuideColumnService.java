package com.tripmarket.domain.guideColumn.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.tripmarket.domain.guide.entity.Guide;
import com.tripmarket.domain.guide.repository.GuideRepository;
import com.tripmarket.domain.guideColumn.dto.GuideColumnRequestDTO;
import com.tripmarket.domain.guideColumn.dto.GuideColumnResponseDTO;
import com.tripmarket.domain.guideColumn.entity.GuideColumn;
import com.tripmarket.domain.guideColumn.repository.GuideColumnRepository;
import com.tripmarket.global.exception.CustomException;
import com.tripmarket.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GuideColumnService {
	private final GuideColumnRepository guideColumnRepository;
	private final GuideRepository guideRepository;
	private final CloudinaryImageService cloudinaryImageService;

	@Transactional
	public GuideColumnResponseDTO createColumn(GuideColumnRequestDTO requestDTO,
		List<MultipartFile> images,
		Long guideId) {
		Guide guide = guideRepository.findById(guideId)
			.orElseThrow(() -> new CustomException(ErrorCode.GUIDE_NOT_FOUND));

		List<String> imageUrls = new ArrayList<>();
		if (images != null && !images.isEmpty()) {
			imageUrls = images.stream()
				.map(cloudinaryImageService::uploadImage)
				.collect(Collectors.toList());
		}

		GuideColumn column = GuideColumn.builder()
			.guide(guide)
			.title(requestDTO.title())
			.content(requestDTO.content())
			.imageUrls(imageUrls)
			.build();

		return GuideColumnResponseDTO.from(guideColumnRepository.save(column));
	}

	public Page<GuideColumnResponseDTO> getColumns(Pageable pageable) {
		return guideColumnRepository.findAllByOrderByCreatedAtDesc(pageable)
			.map(GuideColumnResponseDTO::from);
	}

	public List<GuideColumnResponseDTO> getColumnsByGuide(Long guideId) {
		return guideColumnRepository.findByGuideIdOrderByCreatedAtDesc(guideId)
			.stream()
			.map(GuideColumnResponseDTO::from)
			.collect(Collectors.toList());
	}
}
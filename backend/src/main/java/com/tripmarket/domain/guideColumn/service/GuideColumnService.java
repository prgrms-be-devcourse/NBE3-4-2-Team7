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
import com.tripmarket.global.auth.AuthenticatedUser;
import com.tripmarket.global.exception.CustomException;
import com.tripmarket.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GuideColumnService {
	private final GuideColumnRepository guideColumnRepository;
	private final GuideRepository guideRepository;
	private final CloudinaryImageService cloudinaryImageService;

	@Transactional
	public GuideColumnResponseDTO createColumn(
		GuideColumnRequestDTO requestDTO,
		List<MultipartFile> images,
		AuthenticatedUser user) {
		Guide guide = guideRepository.findById(user.getId())
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

	@Transactional
	public GuideColumnResponseDTO updateColumn(
		Long columnId,
		GuideColumnRequestDTO requestDTO,
		List<MultipartFile> newImages,
		AuthenticatedUser user
	) {
		GuideColumn column = guideColumnRepository.findById(columnId)
			.orElseThrow(() -> new CustomException(ErrorCode.GUIDE_COLUMN_NOT_FOUND));

		if (user.getId() != column.getGuide().getMember().getId()) {
			throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
		}

		// 유지할 이미지 URL들 (requestDTO.imageUrls()에 포함된 것들)
		List<String> keepImageUrls = requestDTO.imageUrls() != null ? requestDTO.imageUrls() : new ArrayList<>();

		// 삭제할 이미지들 처리 (기존 이미지 중 keepImageUrls에 없는 것들)
		column.getImageUrls().stream()
			.filter(url -> !keepImageUrls.contains(url))
			.forEach(cloudinaryImageService::deleteImage);

		// 새로운 이미지 업로드
		List<String> newImageUrls = new ArrayList<>();
		if (newImages != null && !newImages.isEmpty()) {
			newImageUrls = newImages.stream()
				.map(cloudinaryImageService::uploadImage)
				.toList();
		}

		// 유지할 이미지 URL들과 새로 업로드된 이미지 URL들을 합침
		List<String> finalImageUrls = new ArrayList<>(keepImageUrls);
		finalImageUrls.addAll(newImageUrls);

		return GuideColumnResponseDTO.from(
			column.update(requestDTO.title(), requestDTO.content(), finalImageUrls));
	}

	@Transactional
	public void deleteColumn(Long columnId, AuthenticatedUser user) {
		GuideColumn column = guideColumnRepository.findById(columnId)
			.orElseThrow(() -> new CustomException(ErrorCode.GUIDE_COLUMN_NOT_FOUND));

		if (user.getId() != column.getGuide().getMember().getId()) {
			throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
		}

		// 이미지 삭제 처리
		if (!column.getImageUrls().isEmpty()) {
			column.getImageUrls().forEach(cloudinaryImageService::deleteImage);
		}

		guideColumnRepository.delete(column);
	}

	public GuideColumnResponseDTO getColumn(Long columnId) {
		GuideColumn column = guideColumnRepository.findById(columnId)
			.orElseThrow(() -> new CustomException(ErrorCode.GUIDE_COLUMN_NOT_FOUND));

		return GuideColumnResponseDTO.from(column);
	}
}
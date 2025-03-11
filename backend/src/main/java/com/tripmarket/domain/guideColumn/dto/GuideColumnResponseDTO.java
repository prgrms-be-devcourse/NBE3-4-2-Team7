package com.tripmarket.domain.guideColumn.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.tripmarket.domain.guideColumn.entity.GuideColumn;

public record GuideColumnResponseDTO(
	Long id,
	String guideName,
	String title,
	String content,
	List<String> imageUrls,
	LocalDateTime createdAt
) {
	public static GuideColumnResponseDTO from(GuideColumn guideColumn) {
		return new GuideColumnResponseDTO(
			guideColumn.getId(),
			guideColumn.getGuide().getName(),
			guideColumn.getTitle(),
			guideColumn.getContent(),
			guideColumn.getImageUrls(),
			guideColumn.getCreatedAt()
		);
	}
}

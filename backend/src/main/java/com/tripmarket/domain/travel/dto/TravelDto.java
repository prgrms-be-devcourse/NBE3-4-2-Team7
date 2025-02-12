package com.tripmarket.domain.travel.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tripmarket.domain.travel.entity.Travel;
import com.tripmarket.domain.travel.enums.TravelStatus;

public record TravelDto(
	Long id,
	String city,
	String places,
	int participants,
	String content,
	String categoryName,
	TravelStatus status,

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
	LocalDate startDate,

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
	LocalDate endDate,

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
	LocalDateTime createdAt,

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
	LocalDateTime updatedAt
) {
	public static TravelDto of(Travel travel) {
		return new TravelDto(
			travel.getId(),
			travel.getCity(),
			travel.getPlaces(),
			travel.getParticipants(),
			travel.getContent(),
			travel.getCategory().getName(),
			travel.getStatus(),
			travel.getStartDate(),
			travel.getEndDate(),
			travel.getCreatedAt(),
			travel.getUpdatedAt()
		);
	}
}

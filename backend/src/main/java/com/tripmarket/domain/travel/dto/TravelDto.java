package com.tripmarket.domain.travel.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tripmarket.domain.travel.entity.Travel;
import com.tripmarket.domain.travel.entity.Travel.Status;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class TravelDto {
	private Long id;
	private String city;
	private String places;
	private int participants;
	private String content;
	private String categoryName;
	private Travel.Status status;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
	private LocalDate startDate;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
	private LocalDate endDate;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
	private LocalDateTime createdAt;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
	private LocalDateTime updatedAt;

	@Builder
	public TravelDto(Long id, String city, String places, int participants, String content, String categoryName,
		Status status, LocalDate startDate, LocalDate endDate,
		LocalDateTime createdAt, LocalDateTime updatedAt) {
		this.id = id;
		this.city = city;
		this.places = places;
		this.participants = participants;
		this.content = content;
		this.categoryName = categoryName;
		this.status = status;
		this.startDate = startDate;
		this.endDate = endDate;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public static TravelDto of(Travel travel) {
		return TravelDto.builder()
			.id(travel.getId())
			.city(travel.getCity())
			.places(travel.getPlaces())
			.participants(travel.getParticipants())
			.content(travel.getContent())
			.categoryName(travel.getCategory().getName())
			.status(travel.getStatus())
			.startDate(travel.getStartDate())
			.endDate(travel.getEndDate())
			.createdAt(travel.getCreatedAt())
			.updatedAt(travel.getUpdatedAt())
			.build();
	}
}

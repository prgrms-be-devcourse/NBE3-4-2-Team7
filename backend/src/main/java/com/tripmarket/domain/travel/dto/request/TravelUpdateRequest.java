package com.tripmarket.domain.travel.dto.request;

import java.time.LocalDate;

import com.tripmarket.domain.travel.entity.Travel;
import com.tripmarket.domain.travel.entity.TravelCategory;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TravelUpdateRequest {

	@NotNull(message = "카테고리 ID는 필수 입력값입니다.")
	@Min(value = 1, message = "유효하지 않은 카테고리 ID입니다.")
	private Long categoryId;

	@NotBlank(message = "도시는 필수 입력값입니다.")
	@Size(max = 50, message = "도시는 50자 이하로 입력해야 합니다.")
	private String city;

	@NotBlank(message = "관광지 목록은 필수 입력값입니다.")
	@Pattern(regexp = "^[a-zA-Z0-9,\\s]+$", message = "관광지 목록은 쉼표로 구분된 영어, 숫자만 입력 가능합니다.")
	private String places;

	@NotNull(message = "여행 기간은 필수 입력값입니다.")
	private TravelPeriod travelPeriod;

	@NotNull(message = "참여 인원은 필수 입력값입니다.")
	@Min(value = 1, message = "참여 인원은 최소 1명 이상이어야 합니다.")
	private int participants;

	@NotBlank(message = "상세 요청 내용은 필수 입력값입니다.")
	@Size(max = 500, message = "상세 요청 내용은 500자 이하로 입력해야 합니다.")
	private String content;

	@Getter
	@NoArgsConstructor
	public static class TravelPeriod {

		@NotNull(message = "여행 시작 날짜는 필수 입력값입니다.")
		@FutureOrPresent(message = "여행 시작 날짜는 오늘 이후여야 합니다.")
		private LocalDate startDate;

		@NotNull(message = "여행 종료 날짜는 필수 입력값입니다.")
		@Future(message = "여행 종료 날짜는 현재 또는 과거일 수 없습니다.")
		private LocalDate endDate;

		public TravelPeriod(LocalDate startDate, LocalDate endDate) {
			this.startDate = startDate;
			this.endDate = endDate;
		}
	}

	@Builder
	public TravelUpdateRequest(Long categoryId, String city, String places, TravelPeriod travelPeriod, int participants,
		String content) {
		this.categoryId = categoryId;
		this.city = city;
		this.places = places;
		this.travelPeriod = travelPeriod;
		this.participants = participants;
		this.content = content;
	}

	public Travel toEntity(Travel existingTravel, TravelCategory category) {
		return Travel.builder()
			.user(existingTravel.getUser())
			.category(category)
			.city(city)
			.places(places)
			.participants(participants)
			.startDate(travelPeriod.getStartDate())
			.endDate(travelPeriod.getEndDate())
			.content(content)
			.status(Travel.Status.WAITING_FOR_MATCHING)
			.isDeleted(false)
			.build();
	}
}

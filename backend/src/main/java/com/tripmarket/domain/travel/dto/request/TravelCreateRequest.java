package com.tripmarket.domain.travel.dto.request;

import java.time.LocalDate;

import com.tripmarket.domain.member.entity.Member;
import com.tripmarket.domain.travel.entity.Travel;
import com.tripmarket.domain.travel.entity.TravelCategory;
import com.tripmarket.domain.travel.enums.TravelStatus;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record TravelCreateRequest(

	@NotNull(message = "카테고리 ID는 필수 입력값입니다.")
	@Positive(message = "유효하지 않은 카테고리 ID입니다.")
	Long categoryId,

	@NotBlank(message = "도시는 필수 입력값입니다.")
	@Size(max = 50, message = "도시는 50자 이하로 입력해야 합니다.")
	@Pattern(regexp = "^[가-힣a-zA-Z\\s]+$", message = "도시 이름은 한글과 영어만 입력할 수 있습니다.")
	String city,

	@NotBlank(message = "관광지 목록은 필수 입력값입니다.")
	@Pattern(regexp = "^[가-힣a-zA-Z0-9,\\s]+$", message = "관광지 목록은 쉼표로 구분된 한글, 영어, 숫자만 입력 가능합니다.")
	String places,

	@NotNull(message = "여행 기간은 필수 입력값입니다.")
	TravelPeriod travelPeriod,

	@NotNull(message = "참여 인원은 필수 입력값입니다.")
	@Positive(message = "참여 인원은 최소 1명 이상이어야 합니다.")
	int participants,

	@NotBlank(message = "상세 요청 내용은 필수 입력값입니다.")
	@Size(max = 500, message = "상세 요청 내용은 500자 이하로 입력해야 합니다.")
	String content
) {
	public record TravelPeriod(
		@NotNull(message = "여행 시작 날짜는 필수 입력값입니다.")
		@FutureOrPresent(message = "여행 시작 날짜는 오늘 이후여야 합니다.")
		LocalDate startDate,

		@NotNull(message = "여행 종료 날짜는 필수 입력값입니다.")
		@Future(message = "여행 종료 날짜는 현재 또는 과거일 수 없습니다.")
		LocalDate endDate
	) {
	}

	public Travel toEntity(Member member, TravelCategory category) {
		return Travel.builder()
			.user(member)
			.category(category)
			.city(city)
			.places(places)
			.participants(participants)
			.startDate(travelPeriod.startDate())
			.endDate(travelPeriod.endDate())
			.content(content)
			.status(TravelStatus.WAITING_FOR_MATCHING)
			.isDeleted(false)
			.build();
	}
}

package com.tripmarket.domain.match.dto.request;

import com.tripmarket.domain.guide.entity.Guide;
import com.tripmarket.domain.match.entity.GuideRequest;
import com.tripmarket.domain.match.enums.MatchRequestStatus;
import com.tripmarket.domain.member.entity.Member;
import com.tripmarket.domain.travel.entity.Travel;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record GuideRequestCreate(

	@NotNull(message = "여행 ID는 필수 입력값입니다.")
	@Positive(message = "유효하지 않은 여행 ID입니다.")
	Long travelId
) {
	public GuideRequest toEntity(Member member, Guide guide, Travel travel) {
		return GuideRequest.builder()
			.member(member)
			.guide(guide)
			.travel(travel)
			.status(MatchRequestStatus.PENDING)
			.build();
	}
}

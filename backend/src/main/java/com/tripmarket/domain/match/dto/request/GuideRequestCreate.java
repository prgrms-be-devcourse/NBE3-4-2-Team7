package com.tripmarket.domain.match.dto.request;

import com.tripmarket.domain.guide.entity.Guide;
import com.tripmarket.domain.match.entity.GuideRequest;
import com.tripmarket.domain.member.entity.Member;
import com.tripmarket.domain.travel.entity.Travel;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GuideRequestCreate {

	@NotNull(message = "여행 ID는 필수 입력값입니다.")
	@Min(value = 1, message = "유효하지 않은 여행 ID입니다.")
	private Long travelId;

	public GuideRequest toEntity(Member member, Guide guide, Travel travel) {
		return GuideRequest.builder()
			.member(member)
			.guide(guide)
			.travel(travel)
			.status(GuideRequest.RequestStatus.PENDING)
			.build();
	}
}


package com.tripmarket.domain.match.dto;

import com.tripmarket.domain.match.entity.GuideRequest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GuideRequestDto {
	private Long id;
	private Long travelId;
	private String travelCity; // 여행 제목
	private boolean isTravelDeleted; // 여행 삭제 여부
	private Long guideId;
	private String guideName; // 가이드 이름
	private boolean isGuideDeleted; // 가이드 삭제 여부
	private String memberName;
	private String status; // 매칭 상태

	public static GuideRequestDto of(GuideRequest guideRequest) {
		return new GuideRequestDto(
			guideRequest.getId(),
			guideRequest.getTravel().getId(),
			guideRequest.getTravel().getCity(),
			guideRequest.getTravel().isDeleted(),// Travel 제목
			guideRequest.getGuide().getId(),
			guideRequest.getGuide().getName(), // Guide 이름
			guideRequest.getGuide().isDeleted(),// Member 이름
			guideRequest.getMember().getName(),
			guideRequest.getStatus().toString()
		);
	}
}


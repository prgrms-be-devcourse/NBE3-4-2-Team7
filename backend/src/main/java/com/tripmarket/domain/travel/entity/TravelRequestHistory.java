package com.tripmarket.domain.travel.entity;

import com.tripmarket.global.jpa.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class TravelRequestHistory extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "request_id", nullable = false)
	private Travel travel; // 관련된 여행 요청 (TravelRequest와 연관 관계)

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Travel.Status status; // 변경된 상태 (TravelRequest.TravelRequestStatus Enum 사용)

	@Column(nullable = false)
	private String description; // 상태 변경에 대한 상세 메시지
}

package com.tripmarket.domain.match.entity;

import com.tripmarket.domain.guide.entity.Guide;
import com.tripmarket.domain.member.entity.Member;
import com.tripmarket.domain.travel.entity.Travel;
import com.tripmarket.global.exception.CustomException;
import com.tripmarket.global.exception.ErrorCode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GuideRequest {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private Member user; // 요청한 사용자

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "guide_id", nullable = false)
	private Guide guide; // 요청받은 가이더

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "travel_id", nullable = false)
	private Travel travel; // 여행 정보

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private RequestStatus status = RequestStatus.PENDING;

	public void updateStatus(RequestStatus newStatus) {
		if (this.status == RequestStatus.ACCEPTED || this.status == RequestStatus.REJECTED) {
			throw new CustomException(ErrorCode.REQUEST_ALREADY_PROCESSED);
		}

		if (newStatus == RequestStatus.PENDING) {
			throw new CustomException(ErrorCode.INVALID_REQUEST_STATUS);
		}
		this.status = newStatus;

		if (newStatus == RequestStatus.ACCEPTED) {
			this.travel.updateTravelStatus(Travel.Status.MATCHED);
		}

		if (newStatus == RequestStatus.REJECTED) {
			this.travel.updateTravelStatus(Travel.Status.WAITING_FOR_MATCHING);
		}
	}

	public enum RequestStatus {
		PENDING, ACCEPTED, REJECTED
	}
}

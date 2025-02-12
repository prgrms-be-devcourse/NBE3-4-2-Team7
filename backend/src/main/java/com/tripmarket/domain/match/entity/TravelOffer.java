package com.tripmarket.domain.match.entity;

import com.tripmarket.domain.guide.entity.Guide;
import com.tripmarket.domain.match.enums.MatchRequestStatus;
import com.tripmarket.domain.match.util.MatchRequestStatusUpdater;
import com.tripmarket.domain.travel.entity.Travel;

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

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class TravelOffer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "guide_id", nullable = false)
	private Guide guide; // 요청을 보낸 가이더

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "travel_id", nullable = false)
	private Travel travel; // 대상 여행 요청 글

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	@Builder.Default
	private MatchRequestStatus status = MatchRequestStatus.PENDING;

	@Column(columnDefinition = "TEXT")
	private String message;

	public void updateStatus(MatchRequestStatus newStatus) {
		MatchRequestStatusUpdater.updateStatus(this.status, newStatus, this.travel);
		this.status = newStatus;
	}

	public void completeStatus() {
		this.status = MatchRequestStatusUpdater.completeStatus(this.status);
	}
}

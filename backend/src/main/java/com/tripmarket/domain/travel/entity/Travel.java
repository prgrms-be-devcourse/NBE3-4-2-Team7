package com.tripmarket.domain.travel.entity;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tripmarket.domain.member.entity.Member;
import com.tripmarket.global.exception.CustomException;
import com.tripmarket.global.exception.ErrorCode;
import com.tripmarket.global.jpa.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Travel extends BaseEntity {

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private Member user;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id", nullable = false)
	private TravelCategory category;

	@Column(length = 50, nullable = false)
	private String city;

	@Column(columnDefinition = "TEXT", nullable = false)
	private String places;

	@Column(nullable = false)
	private int participants;

	@Column(nullable = false)
	private LocalDate startDate;

	@Column(nullable = false)
	private LocalDate endDate;

	@Column(columnDefinition = "TEXT", nullable = false)
	private String content;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Status status;

	@Builder.Default
	@Column(nullable = false)
	private boolean isDeleted = false;

	public enum Status {
		WAITING_FOR_MATCHING,
		IN_PROGRESS,
		MATCHED;
	}

	@Builder
	public Travel(Member user, TravelCategory category, String city, String places, int participants,
		LocalDate startDate, LocalDate endDate, String content) {
		this.user = user;
		this.category = category;
		this.city = city;
		this.places = places;
		this.participants = participants;
		this.startDate = startDate;
		this.endDate = endDate;
		this.content = content;
	}

	public void updateTravel(Travel travel, TravelCategory category) {
		this.city = travel.getCity();
		this.places = travel.getPlaces();
		this.startDate = travel.getStartDate();
		this.endDate = travel.getEndDate();
		this.participants = travel.getParticipants();
		this.content = travel.getContent();
		this.category = category;
	}

	public void markAsDeleted() {
		this.isDeleted = true;
	}

	public void updateTravelStatus(Status status) {
		if (this.status == Status.MATCHED) {
			throw new CustomException(ErrorCode.TRAVEL_ALREADY_MATCHED);
		}

		this.status = status;
	}
}

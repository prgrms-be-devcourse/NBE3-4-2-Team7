package com.tripmarket.domain.guide.entity;

import com.tripmarket.domain.guide.dto.GuideDto;
import com.tripmarket.domain.member.entity.Member;
import com.tripmarket.global.jpa.entity.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Guide extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "고유 ID")
	private Long id;

	@OneToOne(mappedBy = "guide")
	private Member member; // 가이드와 연결된 회원 정보

	@Column(nullable = false, length = 30)
	@Size(min = 1, max = 30)
	private String name;

	@Column(nullable = false, length = 100)
	@Size(min = 1, max = 100)
	private String activityRegion;

	@Column(nullable = false, length = 300)
	@Size(min = 1, max = 300)
	private String introduction;

	@Column(nullable = false)
	private String languages;

	private Integer experienceYears;

	@Column(nullable = false)
	@Builder.Default
	private boolean isDeleted = false;

	public void setMember(Member member) {
		this.member = member;
	}

	public void updateGuide(GuideDto guideDto) {
		this.activityRegion = guideDto.activityRegion();
		this.introduction = guideDto.introduction();
		this.languages = guideDto.languages();
		this.experienceYears = guideDto.experienceYears();
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public Long getId() {
		return id;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public Member getMember() {
		return member;
	}
}

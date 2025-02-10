package com.tripmarket.domain.guide.entity;

import java.util.List;
import java.util.Objects;

import com.tripmarket.domain.guide.dto.GuideDto;
import com.tripmarket.domain.member.entity.Member;

import com.tripmarket.global.jpa.entity.BaseTimeEntity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Guide extends BaseTimeEntity {

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
	// TODO: 국가 단위면 ISO 국가코드 사용하면 되고, 도시는 어떻게 검증?
	private String activityRegion;

	@Column(nullable = false, length = 300)
	@Size(min = 1, max = 300)
	private String introduction;

	@Column(nullable = false)
	@ValidLanguages
	private String languages;

	@Min(0)
	@Max(100)
	private Integer experienceYears;

	@Column(nullable = false)
	@Builder.Default
	private boolean isDeleted = false;

	// 리뷰 통계 테이블
	// @OneToOne
	// GuideReviewStats guideReviewStats;

	public void setMember(Member member) {
		this.member = member;
	}

	public void updateGuide(GuideDto guideDto) {
		this.activityRegion = guideDto.getActivityRegion();
		this.introduction = guideDto.getIntroduction();
		this.languages = guideDto.getLanguages();
		this.experienceYears = guideDto.getExperienceYears();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Guide guide = (Guide)o;
		return isDeleted == guide.isDeleted
			&& Objects.equals(id, guide.getId())
			&& Objects.equals(name, guide.name)
			&& Objects.equals(activityRegion, guide.activityRegion)
			&& Objects.equals(introduction, guide.introduction)
			&& Objects.equals(languages, guide.languages)
			&& Objects.equals(experienceYears, guide.experienceYears);
	}

}

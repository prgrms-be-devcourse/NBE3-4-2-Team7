package com.tripmarket.domain.guideColumn.entity;

import java.util.ArrayList;
import java.util.List;

import com.tripmarket.domain.guide.entity.Guide;
import com.tripmarket.global.jpa.entity.BaseEntity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GuideColumn extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "guide_id")
	private Guide guide;

	@Column(nullable = false)
	private String title;

	@Column(columnDefinition = "TEXT", nullable = false)
	private String content;

	@ElementCollection
	@CollectionTable(name = "guide_column_images", joinColumns = @JoinColumn(name = "guide_column_id"))
	private List<String> imageUrls = new ArrayList<>();

	@Builder
	public GuideColumn(Guide guide, String title, String content, List<String> imageUrls) {
		this.guide = guide;
		this.title = title;
		this.content = content;
		this.imageUrls = imageUrls;
	}
}
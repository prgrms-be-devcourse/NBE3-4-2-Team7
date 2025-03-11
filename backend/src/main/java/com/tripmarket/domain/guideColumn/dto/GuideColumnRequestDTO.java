package com.tripmarket.domain.guideColumn.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GuideColumnRequestDTO(
	@JsonProperty("title")
	String title,

	@JsonProperty("content")
	String content,

	@JsonProperty("imageUrls")
	List<String> imageUrls
) {
}
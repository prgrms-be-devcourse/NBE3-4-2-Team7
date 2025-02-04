package com.tripmarket.domain.travel.service;

import org.springframework.stereotype.Service;

import com.tripmarket.domain.travel.entity.TravelCategory;
import com.tripmarket.domain.travel.repository.TravelCategoryRepository;
import com.tripmarket.global.exception.CustomException;
import com.tripmarket.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TravelCategoryService {

	private final TravelCategoryRepository travelCategoryRepository;

	public TravelCategory getTravelCategory(Long travelCategoryId) {
		return travelCategoryRepository.findById(travelCategoryId)
			.orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));
	}

}

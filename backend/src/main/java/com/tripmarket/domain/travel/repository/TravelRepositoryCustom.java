package com.tripmarket.domain.travel.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.tripmarket.domain.travel.entity.Travel;

public interface TravelRepositoryCustom {
	Page<Travel> searchTravels(Long categoryId, Pageable pageable);

}

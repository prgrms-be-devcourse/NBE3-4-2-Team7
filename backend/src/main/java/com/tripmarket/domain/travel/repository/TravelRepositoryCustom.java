package com.tripmarket.domain.travel.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.tripmarket.domain.travel.entity.Travel;

public interface TravelRepositoryCustom {
	Page<Travel> searchTravels(Long categoryId, Pageable pageable);

	List<Travel> searchTravelsNoOffset(Long categoryId, LocalDateTime lastCreatedAt, int size);

}

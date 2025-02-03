package com.tripmarket.domain.travel.repository;

import com.tripmarket.domain.travel.entity.TravelCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TravelCategoryRepository extends JpaRepository<TravelCategory, Long> {
}


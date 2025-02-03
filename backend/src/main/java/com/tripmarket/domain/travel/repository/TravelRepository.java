package com.tripmarket.domain.travel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tripmarket.domain.travel.entity.Travel;

@Repository
public interface TravelRepository extends JpaRepository<Travel, Long>, TravelRepositoryCustom {

}

package com.tripmarket.domain.travel.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tripmarket.domain.member.entity.Member;
import com.tripmarket.domain.travel.entity.Travel;

@Repository
public interface TravelRepository extends JpaRepository<Travel, Long>, TravelRepositoryCustom {

	List<Travel> findByUser(Member user);

}

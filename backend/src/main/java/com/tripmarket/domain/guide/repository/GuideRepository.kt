package com.tripmarket.domain.guide.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tripmarket.domain.guide.entity.Guide;

@Repository
public interface GuideRepository extends JpaRepository<Guide, Long> {
	Optional<Guide> findByMemberId(Long memberId);

}

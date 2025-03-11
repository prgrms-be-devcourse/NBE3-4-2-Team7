package com.tripmarket.domain.guide.repository;

import com.tripmarket.domain.guide.entity.Guide
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface GuideRepository : JpaRepository<Guide, Long> {
    fun findByMemberId(memberId: Long): Optional<Guide>

}

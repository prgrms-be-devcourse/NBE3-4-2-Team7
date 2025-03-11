package com.tripmarket.domain.guide.service;

import com.tripmarket.domain.guide.dto.GuideCreateRequest
import com.tripmarket.domain.guide.dto.GuideDto
import com.tripmarket.domain.guide.dto.GuideProfileDto
import com.tripmarket.domain.guide.entity.Guide
import com.tripmarket.domain.guide.repository.GuideRepository
import com.tripmarket.domain.member.repository.MemberRepository
import com.tripmarket.domain.review.service.ReviewService
import com.tripmarket.domain.reviewstats.entity.ReviewStats
import com.tripmarket.domain.reviewstats.repository.ReviewStatsRepository
import com.tripmarket.global.exception.CustomException
import com.tripmarket.global.exception.ErrorCode
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
open class GuideService(
    private val guideRepository: GuideRepository,
    private val memberRepository: MemberRepository,
    private val reviewStatsRepository: ReviewStatsRepository,
    private val reviewService: ReviewService
) {

    fun getGuide(id: Long): Guide {
        return guideRepository.findById(id)
            .orElseThrow { CustomException(ErrorCode.GUIDE_NOT_FOUND) }
    }

    @Transactional
    open fun create(createRequest: GuideCreateRequest, email: String) {
        val member = memberRepository.findByEmail(email)
            .orElseThrow { CustomException(ErrorCode.MEMBER_NOT_FOUND) }

        if (member.hasGuideProfile()) {
            throw CustomException(ErrorCode.ALREADY_HAS_GUIDE_PROFILE)
        }

        val guide = GuideCreateRequest.toEntity(createRequest)
        guide.member = member
        member.addGuideProfile(guide)

        guideRepository.save(guide)
    }

    fun getGuideProfile(id: Long): GuideProfileDto {
        val guide = guideRepository.findById(id)
            .orElseThrow { CustomException(ErrorCode.GUIDE_NOT_FOUND) }

        val reviewStats = reviewStatsRepository.findByGuideId(guide.id)
            .orElseGet { ReviewStats(guide.id, 0L, 0.0) }

        val reviews = reviewService.getReviewsByGuide(guide.id)

        return GuideProfileDto.fromEntity(guide, reviewStats, reviews)
    }

    fun getMyGuideProfile(memberId: Long): GuideProfileDto {
        val guide = guideRepository.findByMemberId(memberId)
            .orElseThrow { CustomException(ErrorCode.GUIDE_PROFILE_NOT_FOUND) }

        val reviewStats = reviewStatsRepository.findByGuideId(guide.id)
            .orElseGet { ReviewStats(guide.id, 0L, 0.0) }

        val reviews = reviewService.getReviewsByGuide(guide.id)

        return GuideProfileDto.fromEntity(guide, reviewStats, reviews)
    }

    @Transactional
    open fun update(memberId: Long, guideDto: GuideDto) {
        val guide = memberRepository.findById(memberId)
            .orElseThrow { CustomException(ErrorCode.MEMBER_NOT_FOUND) }
            .guide ?: throw CustomException(ErrorCode.GUIDE_PROFILE_NOT_FOUND)

        guide.updateGuide(guideDto)
        guideRepository.save(guide)
    }

    fun getAllGuides(): List<GuideProfileDto> {
        return guideRepository.findAll().stream().map { guide ->
            GuideProfileDto.fromEntity(guide, null, null)
        }.toList()
    }


    fun delete(id: Long) {
        val guide = getGuide(id)
        guide.isDeleted = true
        guideRepository.save(guide)
    }

    fun validateMyGuide(memberId: Long, guideId: Long): Boolean {
        val member = memberRepository.findById(memberId)
            .orElseThrow { CustomException(ErrorCode.MEMBER_NOT_FOUND) }

        return member.guide?.id == guideId
    }
}

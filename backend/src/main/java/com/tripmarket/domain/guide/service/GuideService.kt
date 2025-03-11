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

// @Service
// @RequiredArgsConstructor
// public class GuideService {
// 	private final GuideRepository guideRepository;
// 	private final MemberRepository memberRepository;
// 	private final ReviewStatsRepository reviewStatsRepository;
// 	private final ReviewService reviewService;
//
// 	public Guide getGuide(Long id) {
// 		return guideRepository.findById(id)
// 			.orElseThrow(() -> new CustomException(ErrorCode.GUIDE_NOT_FOUND));
// 	}
//
// 	@Transactional
// 	public void create(GuideCreateRequest createRequest, String email) {
// 		Member member = memberRepository.findByEmail(email)
// 			.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
//
// 		// 이미 가이드 프로필이 존재하는지 확인
// 		if (member.hasGuideProfile()) {
// 			throw new CustomException(ErrorCode.ALREADY_HAS_GUIDE_PROFILE);
// 		}
//
// 		Guide guide = GuideCreateRequest.toEntity(createRequest);
//
// 		// 가이드 수정
// 		guide.setMember(member);
// 		// 멤버 수정
// 		member.addGuideProfile(guide);
//
// 		guideRepository.save(guide);
// 	}
//
// 	//다른 사용자가 특정 가이드의 프로필을 조회
// 	public GuideProfileDto getGuideProfile(Long id) {
// 		Guide guide = guideRepository.findById(id)
// 			.orElseThrow(() -> new CustomException(ErrorCode.GUIDE_NOT_FOUND));
//
// 		ReviewStats reviewStats = reviewStatsRepository.findByGuideId(guide.getId())
// 			.orElseGet(() -> new ReviewStats(guide.getId(), 0L, 0.0));
//
// 		List<ReviewResponseDto> reviews = reviewService.getReviewsByGuide(guide.getId());
//
// 		return GuideProfileDto.fromEntity(guide, reviewStats, reviews);
// 	}
//
// 	// 현재 로그인한 사용자가 자신의 가이드 프로필을 조회
// 	public GuideProfileDto getMyGuideProfile(Long memberId) {
// 		Guide guide = guideRepository.findByMemberId(memberId)
// 			.orElseThrow(() -> new CustomException(ErrorCode.GUIDE_PROFILE_NOT_FOUND));
//
// 		ReviewStats reviewStats = reviewStatsRepository.findByGuideId(guide.getId())
// 			.orElseGet(() -> new ReviewStats(guide.getId(), 0L, 0.0));
//
// 		List<ReviewResponseDto> reviews = reviewService.getReviewsByGuide(guide.getId());
//
// 		return GuideProfileDto.fromEntity(guide, reviewStats, reviews);
// 	}
//
// 	@Transactional
// 	public void update(Long memberId, GuideDto guideDto) {
// 		Guide guide = memberRepository.findById(memberId)
// 			.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND))
// 			.getGuide();
// 		guide.updateGuide(guideDto);
// 		guideRepository.save(guide);
// 	}
//
// 	public List<GuideProfileDto> getAllGuides() {
// 		// 현재 리뷰가 일대다 단방향이라 임시로 null
// 		// 양방향 설정시 guide 에서 바로 조회,
// 		// 없으면 별도로 조회 로직 필요
// 		return guideRepository.findAll().stream()
// 			.map(guide -> GuideProfileDto.fromEntity(guide, null, null))
// 			.toList();
// 	}
//
// 	public void delete(Long id) {
// 		// 가이드 가져와서 상태 업데이트
// 		Guide guide = getGuide(id);
// 		guide.setDeleted(true);
// 		guideRepository.save(guide);
// 	}
//
// 	public boolean validateMyGuide(Long memberId, Long guideId) {
// 		Member member = memberRepository.findById(memberId)
// 			.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
//
// 		return Optional.ofNullable(member.getGuide())
// 			.map(guide -> Objects.equals(guide.getId(), guideId))
// 			.orElse(false);
// 	}
//
// }

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

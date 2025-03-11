package com.tripmarket.domain.guide;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.tripmarket.domain.guide.dto.GuideCreateRequest;
import com.tripmarket.domain.guide.dto.GuideDto;
import com.tripmarket.domain.guide.dto.GuideProfileDto;
import com.tripmarket.domain.guide.entity.Guide;
import com.tripmarket.domain.guide.repository.GuideRepository;
import com.tripmarket.domain.guide.service.GuideService;
import com.tripmarket.domain.member.entity.Member;
import com.tripmarket.domain.member.entity.Provider;
import com.tripmarket.domain.member.repository.MemberRepository;
import com.tripmarket.global.exception.CustomException;
import com.tripmarket.global.exception.ErrorCode;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
class GuideServiceTest {
	@Autowired
	private GuideRepository guideRepository;

	@Autowired
	private GuideService guideService;

	@Autowired
	private MemberRepository memberRepository;

	private final String testEmail = "testtest@testtest.com";

	// 테스트 멤버 데이터 생성
	// 가이드 정보는 생성 X
	@BeforeEach
	void setUpMember() {
		memberRepository.findByEmail(testEmail).orElseGet(() -> {
			Member testMember = Member.builder()
				.email(testEmail)
				.name("testtest")
				.providerId("3907503508")
				.provider(Provider.KAKAO)
				.imageUrl("")
				.build();
			return memberRepository.save(testMember);
		});
	}

	@Test
	@DisplayName("자신의 가이드 정보가 생성되어야 한다.")
	@Transactional
	void createGuideSuccess() {
		GuideCreateRequest guideCreateRequest = new GuideCreateRequest(
			"testtest",
			"한국어",
			"서울",
			5,
			"소개글"
		);

		guideService.create(guideCreateRequest, testEmail);
		Member member = memberRepository.findByEmail(testEmail).orElse(null);
		Guide savedGuide = GuideCreateRequest.toEntity(guideCreateRequest);

		assertEquals(savedGuide.getName(), guideCreateRequest.name(), "Name이 일치해야 합니다.");
		assertEquals(savedGuide.getActivityRegion(), guideCreateRequest.activityRegion(), "ActivityRegion이 일치해야 합니다.");
		assertEquals(savedGuide.getIntroduction(), guideCreateRequest.introduction(), "Introduction이 일치해야 합니다.");
		assertEquals(savedGuide.getLanguages(), guideCreateRequest.languages(), "Languages가 일치해야 합니다.");
		assertEquals(savedGuide.getExperienceYears(), guideCreateRequest.experienceYears(),
			"ExperienceYears가 일치해야 합니다.");
	}

	@Test
	@DisplayName("이미 가이드 정보를 생성했으면 CustomException(ALREADY_HAS_GUIDE_PROFILE)이 발생해야한다.")
	@Transactional
	void createGuideFailure() {
		GuideCreateRequest guideCreateRequest = new GuideCreateRequest(
			"testtest",
			"한국어",
			"서울",
			5,
			"소개글"
		);

		guideService.create(guideCreateRequest, testEmail);

		CustomException exception = assertThrows(CustomException.class,
			() -> guideService.create(guideCreateRequest, testEmail));

		assertEquals(ErrorCode.ALREADY_HAS_GUIDE_PROFILE, exception.getErrorCode());
	}

	@Test
	@DisplayName("자신의 가이드 생성 후, 로그인된 정보로 자신의 가이드 정보를 가져올 수 있다.")
	void getGuideByMemberIdSuccess() {
		GuideCreateRequest guideCreateRequest = new GuideCreateRequest(
			"testtest",
			"한국어",
			"서울",
			5,
			"소개글"
		);

		guideService.create(guideCreateRequest, testEmail);
		Member member = memberRepository.findByEmail(testEmail).orElse(null);

		Guide savedGuide = GuideCreateRequest.toEntity(guideCreateRequest);
		Guide foundGuide = guideRepository.findByMemberId(member.getId()).orElse(null);

		assertEquals(savedGuide.getName(), foundGuide.getName(), "Name이 일치해야 합니다.");
		assertEquals(savedGuide.getActivityRegion(), foundGuide.getActivityRegion(), "ActivityRegion이 일치해야 합니다.");
		assertEquals(savedGuide.getIntroduction(), foundGuide.getIntroduction(), "Introduction이 일치해야 합니다.");
		assertEquals(savedGuide.getLanguages(), foundGuide.getLanguages(), "Languages가 일치해야 합니다.");
		assertEquals(savedGuide.getExperienceYears(), foundGuide.getExperienceYears(), "ExperienceYears가 일치해야 합니다.");
		assertEquals(savedGuide.isDeleted(), foundGuide.isDeleted(), "isDeleted 값이 일치해야 합니다.");
	}

	@Test
	@DisplayName("가이드 ID로 가이드를 조회 가능해야한다.")
	void getGuideByGuideIdSuccess() {
		GuideCreateRequest guideCreateRequest = new GuideCreateRequest(
			"testtest",
			"한국어",
			"서울",
			5,
			"소개글"
		);

		guideService.create(guideCreateRequest, testEmail);
		Member member = memberRepository.findByEmail(testEmail).orElse(null);
		Guide guide = guideService.getGuide(member.getGuide().getId());

		assertEquals(guide.getId(), member.getGuide().getId());
	}

	@Test
	@DisplayName("잘못된 가이드 ID로 가이드 조회시 CustomException(GUIDE_NOT_FOUND)가 발생해야한다.")
	void getGuideByGuideIdFailure() {
		CustomException exception = assertThrows(CustomException.class, () -> guideService.getGuide(-1L));

		assertEquals(ErrorCode.GUIDE_NOT_FOUND, exception.getErrorCode());
	}

	@Test
	@DisplayName("가이드 ID로 가이드 프로필 Dto가 조회되어야 한다.")
	void getGuideProfileDtoByIdSuccess() {
		GuideCreateRequest guideCreateRequest = new GuideCreateRequest(
			"testtest",
			"한국어",
			"서울",
			5,
			"소개글"
		);

		guideService.create(guideCreateRequest, testEmail);
		Member member = memberRepository.findByEmail(testEmail).orElse(null);
		GuideProfileDto guideProfileDto = guideService.getGuideProfile(member.getGuide().getId());

		assertEquals(guideCreateRequest.name(), guideProfileDto.name());
		assertEquals(guideCreateRequest.languages(), guideProfileDto.languages());
		assertEquals(guideCreateRequest.activityRegion(), guideProfileDto.activityRegion());
		assertEquals(guideCreateRequest.experienceYears(), guideProfileDto.experienceYears());
		assertEquals(guideCreateRequest.introduction(), guideProfileDto.introduction());
	}

	@Test
	@DisplayName("잘못된 가이드 ID로 가이드 프로필 조회 시 CustomException(GUIDE_NOT_FOUND)이 발생해야한다.")
	void getGuideProfileDtoGuideNotFoundFailure() {
		CustomException exception = assertThrows(CustomException.class, () -> guideService.getGuideProfile(-1L));
		assertEquals(ErrorCode.GUIDE_NOT_FOUND, exception.getErrorCode());
	}

	@Test
	@DisplayName("로그인된 정보로 내 가이드 프로필이 조회되어야 한다.")
	void getMyGuideProfile() {
		GuideCreateRequest guideCreateRequest = new GuideCreateRequest(
			"testtest",
			"한국어",
			"서울",
			5,
			"소개글"
		);

		guideService.create(guideCreateRequest, testEmail);

		Member member = memberRepository.findByEmail(testEmail).orElse(null);
		GuideProfileDto guideProfileDto = guideService.getMyGuideProfile(member.getId());
		Guide guide = member.getGuide();

		assertEquals(guide.getName(), guideProfileDto.name());
		assertEquals(guide.getLanguages(), guideProfileDto.languages());
		assertEquals(guide.getActivityRegion(), guideProfileDto.activityRegion());
		assertEquals(guide.getIntroduction(), guideProfileDto.introduction());
		assertEquals(guide.getExperienceYears(), guideProfileDto.experienceYears());
	}

	@Test
	@DisplayName("가이드 프로필이 없는 유저가 자신의 가이드 프로필 조회 시 CustomException(GUIDE_PROFILE_NOT_FOUND)이 발생해야한다.")
	void getMyGuideProfileFailure() {
		Member member = memberRepository.findByEmail(testEmail).orElse(null);
		CustomException exception = assertThrows(CustomException.class,
			() -> guideService.getMyGuideProfile(member.getId()));
		assertEquals(ErrorCode.GUIDE_PROFILE_NOT_FOUND, exception.getErrorCode());
	}

	@Test
	@DisplayName("가이드 정보가 수정되어야한다.")
	void updateSuccess() {
		GuideCreateRequest guideCreateRequest = new GuideCreateRequest(
			"testtest11",
			"한국어",
			"서울",
			5,
			"소개글"
		);
		guideService.create(guideCreateRequest, testEmail);
		Member member = memberRepository.findByEmail(testEmail).orElse(null);
		GuideDto guideDto = new GuideDto(
			"영어",
			"인천",
			5,
			"소개글2",
			false
		);

		guideService.update(member.getId(), guideDto);
		Guide updatedGuide = guideRepository.findByMemberId(member.getId()).orElse(null);

		assertEquals(guideDto.languages(), updatedGuide.getLanguages());
		assertEquals(guideDto.activityRegion(), updatedGuide.getActivityRegion());
		assertEquals(guideDto.introduction(), updatedGuide.getIntroduction());
		assertEquals(guideDto.experienceYears(), updatedGuide.getExperienceYears());
	}

	@Test
	@DisplayName("가이드 프로필이 없는 유저가 가이드 프로필을 수정하는 경우 CustomException(GUIDE_PROFILE_NOT_FOUND)가 발생해야한다.")
	void updateFailure() {
		Member member = memberRepository.findByEmail(testEmail).orElse(null);
		GuideDto guideDto = new GuideDto(
			"영어",
			"인천",
			5,
			"소개글2",
			false
		);
		CustomException exception = assertThrows(CustomException.class,
			() -> guideService.update(member.getId(), guideDto));
		assertEquals(ErrorCode.GUIDE_PROFILE_NOT_FOUND, exception.getErrorCode());

	}

	@Test
	@DisplayName("모든 가이드 목록이 조회되어야 한다. (테스트 데이터 사이즈 1)")
	void getAllGuides() {
		GuideCreateRequest guideCreateRequest = new GuideCreateRequest(
			"testtest",
			"한국어",
			"서울",
			5,
			"소개글"
		);

		guideService.create(guideCreateRequest, testEmail);
		List<GuideProfileDto> list = guideService.getAllGuides();

		assertEquals(1, list.size());
	}

	@Test
	@DisplayName("내 가이드 정보가 삭제되어야 한다.")
	@Transactional
	void deleteGuideSuccess() {
		GuideCreateRequest guideCreateRequest = new GuideCreateRequest(
			"testtest",
			"한국어",
			"서울",
			5,
			"소개글"
		);

		guideService.create(guideCreateRequest, testEmail);

		Member member = memberRepository.findByEmail(testEmail).orElse(null);
		assertTrue(member.hasGuideProfile());

		guideService.delete(member.getId());

		assertFalse(member.hasGuideProfile());
	}

	@Test
	@DisplayName("자신의 가이드 정보이면 true가 반환된다.")
	@Transactional
	void validateMyGuideTrue() {
		GuideCreateRequest guideCreateRequest = new GuideCreateRequest(
			"testtest",
			"한국어",
			"서울",
			5,
			"소개글"
		);

		guideService.create(guideCreateRequest, testEmail);

		// 자신의 로그인 정보
		Member member = memberRepository.findByEmail(testEmail).orElse(null);
		Guide guide = guideRepository.findByMemberId(member.getId()).orElse(null);

		boolean result = guideService.validateMyGuide(member.getId(), guide.getId());
		assertTrue(result);
	}

	@Test
	@DisplayName("자신의 가이드 정보에 대한 상세정보 요청이 아니면 false가 반환된다.")
	@Transactional
	void validateMyGuideFalse() {
		Member testMember2 = Member.builder()
			.email("test2test2@test2.com")
			.name("test2test2")
			.providerId("3907503511")
			.provider(Provider.KAKAO)
			.imageUrl("")
			.build();
		memberRepository.save(testMember2);

		GuideCreateRequest guideCreateRequest1 = new GuideCreateRequest(
			"testtest11",
			"한국어",
			"서울",
			5,
			"소개글"
		);
		GuideCreateRequest guideCreateRequest2 = new GuideCreateRequest(
			"testtest22",
			"한국어",
			"서울",
			10,
			"소개글"
		);

		guideService.create(guideCreateRequest1, "test2test2@test2.com");
		guideService.create(guideCreateRequest2, testEmail);

		Member testMember1 = memberRepository.findByEmail(testEmail).orElse(null);

		boolean result = guideService.validateMyGuide(testMember2.getId(), testMember1.getGuide().getId());

		assertFalse(result);
	}
}
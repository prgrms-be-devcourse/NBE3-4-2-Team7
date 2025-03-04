// package com.example.backend;
//
// import static org.junit.jupiter.api.Assertions.*;
//
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.transaction.annotation.Transactional;
//
// import com.tripmarket.domain.guide.dto.GuideCreateRequest;
// import com.tripmarket.domain.guide.dto.GuideDto;
// import com.tripmarket.domain.guide.entity.Guide;
// import com.tripmarket.domain.guide.repository.GuideRepository;
// import com.tripmarket.domain.guide.service.GuideService;
// import com.tripmarket.domain.member.entity.Member;
// import com.tripmarket.domain.member.repository.MemberRepository;
//
// @SpringBootTest
// class BackendApplicationTests {
//
// 	/*
// 	 * author : 전병규
// 	 *
// 	 * 통합테스트 가이드 시나리오(생성, 수정) 작성
// 	 * */
// 	@Autowired
// 	private GuideService guideService;
//
// 	@Autowired
// 	private GuideRepository guideRepository;
//
// 	@Autowired
// 	private MemberRepository memberRepository;
//
// 	@Test
// 	@DisplayName("특정 유저의 가이드 정보가 생성되어야 한다.")
// 	@Transactional
// 	void createGuideTest() {
// 		// data.sql 에 존재하는 1번 멤버 활용
// 		// Long memberId = 1L;
// 		//
// 		// GuideDto guideDto = GuideDto.builder()
// 		// 	.userId(memberId)
// 		// 	.name("test1")
// 		// 	.introduction("test1")
// 		// 	.activityRegion("korea")
// 		// 	.languages("kr")
// 		// 	.experienceYears(5)
// 		// 	.build();
// 		//
// 		// guideService.create( guideDto);
// 		// Member member = memberRepository.findById(memberId).orElseGet(null);
// 		//
// 		// Guide guide = guideRepository.findById(member.getGuide().getId()).orElseGet(null);
// 		//
// 		// // 멤버는 연관관계 설정이 잘 되어있는지 검사
// 		// assertEquals(true, member.getHasGuideProfile());
// 		// assertEquals(guide, member.getGuide());
// 		//
// 		// // 가이드는 Dto 와 일치하는지만 검사
// 		// assertEquals(guide.getName(), guideDto.name());
// 		// assertEquals(guide.getIntroduction(), guideDto.introduction());
// 		// assertEquals(guide.getActivityRegion(), guideDto.activityRegion());
// 		// assertEquals(guide.getLanguages(), guideDto.languages());
// 		// assertEquals(guide.getExperienceYears(), guideDto.experienceYears());
// 	}
//
// 	@Test
// 	@DisplayName("가이드 정보가 수정되어야 한다.")
// 	@Transactional
// 	void updateGuideTest() {
// 		// data.sql 에 2번만 가이드 프로필 있어서 2번 활용
// 		Long memberId = 2L;
// 		Member member = memberRepository.findById(memberId).orElseGet(null);
//
// 		GuideCreateRequest guideDto = GuideCreateRequest.builder()
// 			.name(member.getName())
// 			.introduction("updateGuideTest")
// 			.activityRegion("US")
// 			.languages("en")
// 			.experienceYears(10)
// 			.build();
//
// 		guideService.update(memberId, guideDto);
//
// 		member = memberRepository.findById(memberId).orElseGet(null);
// 		Guide guideFromMember = member.getGuide();
//
// 		// 멤버 테이블의 Guide와 업데이트용 정보 비교하기
// 		assertEquals(guideDto.id(), guideFromMember.getIntroduction());
// 		assertEquals(guideDto.activityRegion(), guideFromMember.getActivityRegion());
// 		assertEquals(guideDto.languages(), guideFromMember.getLanguages());
// 		assertEquals(guideDto.experienceYears(), guideFromMember.getExperienceYears());
//
// 	}
//
// }

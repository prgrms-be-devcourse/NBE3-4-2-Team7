package com.tripmarket.domain.member.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tripmarket.domain.guide.entity.Guide;
import com.tripmarket.domain.guide.repository.GuideRepository;
import com.tripmarket.domain.match.dto.GuideRequestDto;
import com.tripmarket.domain.match.dto.TravelOfferDto;
import com.tripmarket.domain.match.entity.GuideRequest;
import com.tripmarket.domain.match.entity.TravelOffer;
import com.tripmarket.domain.match.enums.MatchRequestStatus;
import com.tripmarket.domain.match.repository.GuideRequestRepository;
import com.tripmarket.domain.match.repository.TravelOfferRepository;
import com.tripmarket.domain.member.entity.Member;
import com.tripmarket.domain.member.entity.Provider;
import com.tripmarket.domain.member.repository.MemberRepository;
import com.tripmarket.domain.travel.dto.TravelDto;
import com.tripmarket.domain.travel.entity.Travel;
import com.tripmarket.domain.travel.entity.TravelCategory;
import com.tripmarket.domain.travel.enums.TravelStatus;
import com.tripmarket.domain.travel.repository.TravelRepository;
import com.tripmarket.global.exception.CustomException;
import com.tripmarket.global.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private GuideRequestRepository guideRequestRepository;

	@Mock
	private TravelRepository travelRepository;

	@Mock
	private TravelOfferRepository travelOfferRepository;

	@Mock
	private GuideRepository guideRepository;

	@InjectMocks
	private MemberService memberService;

	private Member memberWithGuideProfile;
	private Member memberWithoutGuideProfile;
	private Guide testGuide;
	private Travel testTravel;
	private TravelCategory testCategory;
	private GuideRequest testGuideRequest;
	private TravelOffer testTravelOffer;

	@BeforeEach
	void setUp() {
		// 테스트용 멤버 생성
		memberWithoutGuideProfile = Member.builder()
			.email("user@test.com")
			.name("테스트유저")
			.password("password123!")
			.provider(Provider.LOCAL)
			.providerId(null)
			.imageUrl(null)
			.build();

		memberWithoutGuideProfile.setId(2L);

		// 테스트용 멤버 생성
		memberWithGuideProfile = Member.builder()
			.email("guider@test.com")
			.name("테스트유저")
			.password("password123!")
			.provider(Provider.LOCAL)
			.providerId(null)
			.imageUrl(null)
			.build();

		memberWithGuideProfile.setId(1L);

		// 테스트용 카테고리 생성
		testCategory = new TravelCategory("자연");

		// 테스트용 가이드 생성
		testGuide = Guide.builder()
			.id(1L)
			.name("테스트가이드")
			.activityRegion("서울")
			.introduction("가이드 소개")
			.languages("ko,en")
			.experienceYears(3)
			.build();

		memberWithGuideProfile.addGuideProfile(testGuide);
		testGuide.setMember(memberWithGuideProfile);

		// 테스트용 여행 생성
		testTravel = Travel.builder()
			.id(1L)
			.user(memberWithGuideProfile)
			.category(testCategory)
			.city("서울")
			.places("경복궁, 남산타워")
			.participants(2)
			.startDate(LocalDate.now().plusDays(1))
			.endDate(LocalDate.now().plusDays(3))
			.content("테스트 여행 내용")
			.status(TravelStatus.WAITING_FOR_MATCHING)
			.isDeleted(false)
			.build();

		// 테스트용 가이드 요청 생성
		testGuideRequest = GuideRequest.builder()
			.id(1L)
			.member(memberWithGuideProfile)
			.guide(testGuide)
			.status(MatchRequestStatus.PENDING)
			.travel(testTravel)
			.build();

		// 테스트용 여행 제안 생성
		testTravelOffer = TravelOffer.builder()
			.id(1L)
			.travel(testTravel)
			.guide(testGuide)
			.status(MatchRequestStatus.PENDING)
			.message("여행 제안 메시지")
			.build();
	}

	@Test
	@DisplayName("ID로 회원 조회 성공")
	void getMemberById_success() {
		// given
		when(memberRepository.findById(2L)).thenReturn(Optional.of(memberWithoutGuideProfile));

		// when
		Member foundMember = memberService.getMemberById(2L);

		// then
		assertThat(foundMember).isNotNull();
		assertThat(foundMember.getId()).isEqualTo(2L);
		assertThat(foundMember.getEmail()).isEqualTo("user@test.com");

		verify(memberRepository).findById(2L);
	}

	@Test
	@DisplayName("ID로 회원 조회 실패 - 존재하지 않는 회원")
	void getMemberById_fail_notFound() {
		// given
		when(memberRepository.findById(999L)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> memberService.getMemberById(999L))
			.isInstanceOf(CustomException.class)
			.satisfies(exception -> {
				CustomException customException = (CustomException)exception;
				assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
			});

		verify(memberRepository).findById(999L);
	}

	@Test
	@DisplayName("이메일로 회원 조회 성공")
	void getMemberByEmail_success() {
		// given
		when(memberRepository.findByEmail("user@test.com")).thenReturn(Optional.of(memberWithoutGuideProfile));

		// when
		Member foundMember = memberService.getMemberByEmail("user@test.com");

		// then
		assertThat(foundMember).isNotNull();
		assertThat(foundMember.getEmail()).isEqualTo("user@test.com");

		verify(memberRepository).findByEmail("user@test.com");
	}

	@Test
	@DisplayName("이메일로 회원 조회 실패 - 존재하지 않는 회원")
	void getMemberByEmail_fail_notFound() {
		// given
		when(memberRepository.findByEmail("wrong@test.com")).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> memberService.getMemberByEmail("wrong@test.com"))
			.isInstanceOf(CustomException.class)
			.satisfies(exception -> {
				CustomException customException = (CustomException)exception;
				assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
			});

		verify(memberRepository).findByEmail("wrong@test.com");
	}

	@Test
	@DisplayName("사용자의 가이드 요청 목록 조회 성공")
	void getGuideRequestsByRequester_success() {
		// given
		when(memberRepository.findByEmail("user@test.com")).thenReturn(Optional.of(memberWithoutGuideProfile));
		when(guideRequestRepository.findDetailedByMemberId(2L)).thenReturn(List.of(testGuideRequest));

		// when
		List<GuideRequestDto> guideRequests = memberService.getGuideRequestsByRequester("user@test.com");

		// then
		assertThat(guideRequests).isNotEmpty();
		assertThat(guideRequests).hasSize(1);
		assertThat(guideRequests.get(0).id()).isEqualTo(1L);

		verify(memberRepository).findByEmail("user@test.com");
		verify(guideRequestRepository).findDetailedByMemberId(2L);
	}

	@Test
	@DisplayName("사용자의 여행 목록 조회 성공")
	void getMyTravels_success() {
		// given
		when(memberRepository.findByEmail("user@test.com")).thenReturn(Optional.of(memberWithoutGuideProfile));
		when(travelRepository.findByUser(memberWithoutGuideProfile)).thenReturn(List.of(testTravel));

		// when
		List<TravelDto> travels = memberService.getMyTravels("user@test.com");

		// then
		assertThat(travels).isNotEmpty();
		assertThat(travels).hasSize(1);
		assertThat(travels.get(0).id()).isEqualTo(1L);
		assertThat(travels.get(0).city()).isEqualTo("서울");

		verify(memberRepository).findByEmail("user@test.com");
		verify(travelRepository).findByUser(memberWithoutGuideProfile);
	}

	@Test
	@DisplayName("사용자의 여행 제안 목록 조회 성공")
	void getTravelOffersForUser_success() {
		// given
		when(memberRepository.findByEmail("user@test.com")).thenReturn(Optional.of(memberWithoutGuideProfile));
		when(travelRepository.findByUser(memberWithoutGuideProfile)).thenReturn(List.of(testTravel));
		when(travelOfferRepository.findByTravelIdIn(List.of(1L))).thenReturn(List.of(testTravelOffer));

		// when
		List<TravelOfferDto> travelOffers = memberService.getTravelOffersForUser("user@test.com");

		// then
		assertThat(travelOffers).isNotEmpty();
		assertThat(travelOffers).hasSize(1);
		assertThat(travelOffers.get(0).id()).isEqualTo(1L);

		verify(memberRepository).findByEmail("user@test.com");
		verify(travelRepository).findByUser(memberWithoutGuideProfile);
		verify(travelOfferRepository).findByTravelIdIn(List.of(1L));
	}

	@Test
	@DisplayName("사용자의 여행 제안 목록 조회 - 여행이 없는 경우")
	void getTravelOffersForUser_emptyTravels() {
		// given
		when(memberRepository.findByEmail("user@test.com")).thenReturn(Optional.of(memberWithoutGuideProfile));
		when(travelRepository.findByUser(memberWithoutGuideProfile)).thenReturn(Collections.emptyList());

		// when
		List<TravelOfferDto> travelOffers = memberService.getTravelOffersForUser("user@test.com");

		// then
		assertThat(travelOffers).isEmpty();

		verify(memberRepository).findByEmail("user@test.com");
		verify(travelRepository).findByUser(memberWithoutGuideProfile);
		verify(travelOfferRepository, never()).findByTravelIdIn(anyList());
	}

	@Test
	@DisplayName("가이드에게 온 가이드 요청 목록 조회 성공")
	void getGuideRequestsByGuide_success() {
		// given
		when(memberRepository.findByEmail("guider@test.com")).thenReturn(Optional.of(memberWithGuideProfile));
		when(guideRequestRepository.findByGuideId(1L)).thenReturn(List.of(testGuideRequest));

		// when
		List<GuideRequestDto> guideRequests = memberService.getGuideRequestsByGuide("guider@test.com");

		// then
		assertThat(guideRequests).isNotEmpty();
		assertThat(guideRequests).hasSize(1);

		verify(memberRepository).findByEmail("guider@test.com");
		verify(guideRequestRepository).findByGuideId(1L);
	}

	@Test
	@DisplayName("가이드가 보낸 여행 제안 목록 조회 성공")
	void getTravelOffersByGuide_success() {
		// given
		when(memberRepository.findByEmail("guider@test.com")).thenReturn(Optional.of(memberWithGuideProfile));
		when(travelOfferRepository.findByGuideId(1L)).thenReturn(List.of(testTravelOffer));

		// when
		List<TravelOfferDto> travelOffers = memberService.getTravelOffersByGuide("guider@test.com");

		// then
		assertThat(travelOffers).isNotEmpty();
		assertThat(travelOffers).hasSize(1);

		verify(memberRepository).findByEmail("guider@test.com");
		verify(travelOfferRepository).findByGuideId(1L);
	}

	@Test
	@DisplayName("사용자의 가이드 프로필 존재 여부 확인 - 존재하는 경우")
	void hasGuideProfile_exists() {
		// given
		when(guideRepository.findByMemberId(1L)).thenReturn(Optional.of(testGuide));

		// when
		boolean hasProfile = memberService.hasGuideProfile(1L);

		// then
		assertThat(hasProfile).isTrue();

		verify(guideRepository).findByMemberId(1L);
	}

	@Test
	@DisplayName("사용자의 가이드 프로필 존재 여부 확인 - 존재하지 않는 경우")
	void hasGuideProfile_notExists() {
		// given
		when(guideRepository.findByMemberId(1L)).thenReturn(Optional.empty());

		// when
		boolean hasProfile = memberService.hasGuideProfile(1L);

		// then
		assertThat(hasProfile).isFalse();

		verify(guideRepository).findByMemberId(1L);
	}
}
package com.example.backend.domain.travel;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

import com.tripmarket.domain.member.entity.Member;
import com.tripmarket.domain.member.service.MemberService;
import com.tripmarket.domain.travel.dto.TravelDto;
import com.tripmarket.domain.travel.dto.request.TravelCreateRequest;
import com.tripmarket.domain.travel.dto.request.TravelUpdateRequest;
import com.tripmarket.domain.travel.entity.Travel;
import com.tripmarket.domain.travel.entity.TravelCategory;
import com.tripmarket.domain.travel.enums.TravelStatus;
import com.tripmarket.domain.travel.repository.TravelRepository;
import com.tripmarket.domain.travel.service.TravelCategoryService;
import com.tripmarket.domain.travel.service.TravelService;
import com.tripmarket.global.exception.CustomException;
import com.tripmarket.global.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
class TravelServiceTest {

	@Mock
	private MemberService memberService;

	@Mock
	private TravelCategoryService travelCategoryService;

	@Mock
	private TravelRepository travelRepository;

	@InjectMocks
	private TravelService travelService;

	@Test
	@DisplayName("✅ 정상 테스트 : 여행 글 생성 성공")
	void testCreateTravel_Success() {
		// Given
		Long userId = 1L;
		Long categoryId = 2L;
		TravelCreateRequest request = new TravelCreateRequest(
			categoryId,
			"Paris",
			"Eiffel Tower, Louvre Museum",
			new TravelCreateRequest.TravelPeriod(LocalDate.of(2025, 5, 10), LocalDate.of(2025, 5, 15)),
			2,
			"파리 야경을 중심으로 여행하고 싶어요."
		);

		Member mockMember = new Member("힐링", "asd@naver.com", "testPassword", "John Doe", null);
		TravelCategory mockCategory = new TravelCategory("힐링");
		Travel mockTravel = request.toEntity(mockMember, mockCategory);

		when(memberService.getMemberById(userId)).thenReturn(mockMember);
		when(travelCategoryService.getTravelCategory(categoryId)).thenReturn(mockCategory);
		when(travelRepository.save(any(Travel.class))).thenReturn(mockTravel);

		// When
		TravelDto result = travelService.createTravel(userId, request);

		// Then
		assertNotNull(result);
		assertEquals("Paris", result.getCity());
		assertEquals(2, result.getParticipants());
		assertEquals(TravelStatus.WAITING_FOR_MATCHING, result.status());

		verify(memberService, times(1)).getMemberById(userId);
		verify(travelCategoryService, times(1)).getTravelCategory(categoryId);
		verify(travelRepository, times(1)).save(any(Travel.class));
	}

	@Test
	@DisplayName("❌ 예외 테스트: 존재하지 않는 사용자")
	void testCreateTravel_MemberNotFound() {
		// Given
		Long userId = 99L;
		TravelCreateRequest request = TravelCreateRequest.builder()
			.categoryId(1L)
			.city("Paris")
			.places("Eiffel Tower, Louvre Museum")
			.travelPeriod(new TravelCreateRequest.TravelPeriod(
				LocalDate.of(2025, 5, 10),
				LocalDate.of(2025, 5, 15)
			))
			.participants(2)
			.content("파리 야경을 중심으로 여행하고 싶어요.")
			.build();

		when(memberService.getMemberById(userId)).thenThrow(new CustomException(ErrorCode.MEMBER_NOT_FOUND));

		// When & Then
		CustomException exception = assertThrows(CustomException.class, () -> {
			travelService.createTravel(userId, request);
		});

		assertEquals(ErrorCode.MEMBER_NOT_FOUND, exception.getErrorCode());
		verify(travelRepository, never()).save(any(Travel.class));
	}

	@Test
	@DisplayName("❌ 예외 테스트: 존재하지 않는 카테고리")
	void testCreateTravel_CategoryNotFound() {
		// Given
		Long userId = 1L;
		Long invalidCategoryId = 999L;
		TravelCreateRequest request = TravelCreateRequest.builder()
			.categoryId(invalidCategoryId)
			.city("Paris")
			.places("Eiffel Tower, Louvre Museum")
			.travelPeriod(new TravelCreateRequest.TravelPeriod(
				LocalDate.of(2025, 5, 10),
				LocalDate.of(2025, 5, 15)
			))
			.participants(2)
			.content("파리 야경을 중심으로 여행하고 싶어요.")
			.build();

		Member mockMember = new Member("힐링", "asd@naver.com", "testPassword", "John Doe", null);

		when(memberService.getMemberById(userId)).thenReturn(mockMember);
		when(travelCategoryService.getTravelCategory(invalidCategoryId))
			.thenThrow(new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

		// When & Then
		CustomException exception = assertThrows(CustomException.class, () -> {
			travelService.createTravel(userId, request);
		});

		assertEquals(ErrorCode.CATEGORY_NOT_FOUND, exception.getErrorCode());
		verify(travelRepository, never()).save(any(Travel.class));
	}

	@Test
	@DisplayName("✅ 정상 테스트: 여행 수정 성공")
	void testUpdateTravel_Success() {
		// Given
		Long userId = 1L;
		Long travelId = 10L;
		Long categoryId = 2L;

		Travel mockTravel = mock(Travel.class);
		TravelCategory mockCategory = new TravelCategory("힐링");
		Member mockMember = mock(Member.class);

		when(mockMember.getId()).thenReturn(userId);
		when(mockTravel.getUser()).thenReturn(mockMember);
		when(mockTravel.getStatus()).thenReturn(TravelStatus.WAITING_FOR_MATCHING);
		when(travelCategoryService.getTravelCategory(categoryId)).thenReturn(mockCategory);
		when(travelRepository.findById(travelId)).thenReturn(Optional.of(mockTravel));
		when(mockTravel.getCategory()).thenReturn(mockCategory);

		TravelUpdateRequest request = TravelUpdateRequest.builder()
			.categoryId(categoryId)
			.city("New York")
			.places("Statue of Liberty, Times Square")
			.travelPeriod(new TravelUpdateRequest.TravelPeriod(
				LocalDate.of(2025, 6, 1),
				LocalDate.of(2025, 6, 7)
			))
			.participants(3)
			.content("뉴욕에서 다양한 문화 체험을 하고 싶어요.")
			.build();

		// When
		TravelDto updatedTravel = travelService.updateTravel(travelId, userId, request);

		// Then
		assertNotNull(updatedTravel);
	}

	@Test
	@DisplayName("❌ 예외 테스트: 본인이 작성한 여행 글이 아닌 경우")
	void testUpdateTravel_NotOwner() {
		// Given
		Long userId = 1L;
		Long anotherUserId = 2L;
		Long travelId = 10L;

		Travel mockTravel = mock(Travel.class);
		Member mockMember = mock(Member.class);

		when(mockMember.getId()).thenReturn(anotherUserId);
		when(mockTravel.getUser()).thenReturn(mockMember);
		when(travelRepository.findById(travelId)).thenReturn(Optional.of(mockTravel));

		TravelUpdateRequest request = TravelUpdateRequest.builder()
			.categoryId(2L)
			.city("New York")
			.places("Statue of Liberty, Times Square")
			.travelPeriod(new TravelUpdateRequest.TravelPeriod(LocalDate.of(2025, 6, 1), LocalDate.of(2025, 6, 7)))
			.participants(3)
			.content("뉴욕에서 다양한 문화 체험을 하고 싶어요.")
			.build();

		// When & Then
		CustomException exception = assertThrows(CustomException.class, () -> {
			travelService.updateTravel(travelId, userId, request);
		});

		assertEquals(ErrorCode.TRAVEL_ACCESS_DENIED, exception.getErrorCode());
	}

	@Test
	@DisplayName("❌ 예외 테스트: 이미 매칭된 여행 글을 수정하려는 경우")
	void testUpdateTravel_AlreadyMatched() {
		// Given
		Long userId = 1L;
		Long travelId = 10L;

		Travel mockTravel = mock(Travel.class);
		Member mockMember = mock(Member.class);

		when(mockMember.getId()).thenReturn(userId);
		when(mockTravel.getUser()).thenReturn(mockMember);
		when(mockTravel.getStatus()).thenReturn(TravelStatus.IN_PROGRESS);
		when(travelRepository.findById(travelId)).thenReturn(Optional.of(mockTravel));

		TravelUpdateRequest request = TravelUpdateRequest.builder()
			.categoryId(2L)
			.city("New York")
			.places("Statue of Liberty, Times Square")
			.travelPeriod(new TravelUpdateRequest.TravelPeriod(LocalDate.of(2025, 6, 1), LocalDate.of(2025, 6, 7)))
			.participants(3)
			.content("뉴욕에서 다양한 문화 체험을 하고 싶어요.")
			.build();

		// When & Then
		CustomException exception = assertThrows(CustomException.class, () -> {
			travelService.updateTravel(travelId, userId, request);
		});

		assertEquals(ErrorCode.TRAVEL_ALREADY_IN_PROGRESS, exception.getErrorCode());
	}

	@Test
	@DisplayName("❌ 예외 테스트: 유효하지 않은 카테고리 ID")
	void testUpdateTravel_InvalidCategory() {
		// Given
		Long userId = 1L;
		Long travelId = 10L;
		Long invalidCategoryId = 999L;

		Travel mockTravel = mock(Travel.class);
		Member mockMember = mock(Member.class);

		TravelUpdateRequest request = TravelUpdateRequest.builder()
			.categoryId(invalidCategoryId)
			.city("New York")
			.places("Statue of Liberty, Times Square")
			.travelPeriod(new TravelUpdateRequest.TravelPeriod(LocalDate.of(2025, 6, 1), LocalDate.of(2025, 6, 7)))
			.participants(3)
			.content("뉴욕에서 다양한 문화 체험을 하고 싶어요.")
			.build();

		when(mockMember.getId()).thenReturn(userId);
		when(mockTravel.getUser()).thenReturn(mockMember);
		when(mockTravel.getStatus()).thenReturn(TravelStatus.WAITING_FOR_MATCHING);
		when(travelRepository.findById(travelId)).thenReturn(Optional.of(mockTravel));
		when(travelCategoryService.getTravelCategory(invalidCategoryId))
			.thenThrow(new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

		// When & Then
		CustomException exception = assertThrows(CustomException.class, () -> {
			travelService.updateTravel(travelId, userId, request);
		});

		assertEquals(ErrorCode.CATEGORY_NOT_FOUND, exception.getErrorCode());
	}

	@Test
	@DisplayName("✅ 정상 테스트: 카테고리 없이 여행 목록 조회")
	void testGetTravels_WithoutCategory() {
		// Given
		Pageable pageable = PageRequest.of(0, 5, Sort.by("createdAt").descending());
		List<Travel> mockTravelList = Arrays.asList(
			Travel.builder()
				.user(new Member("힐링", "asd@naver.com", "testPassword", "John Doe", null))
				.category(new TravelCategory("힐링"))
				.city("Paris")
				.places("Eiffel Tower, Louvre Museum")
				.participants(3)
				.startDate(LocalDate.now())
				.endDate(LocalDate.now().plusDays(5))
				.content("파리 야경 여행")
				.build(),
			Travel.builder()
				.user(new Member("힐링", "asd@naver.com", "testPassword", "John Doe", null))
				.category(new TravelCategory("자연"))
				.places("Gyeongbokgung, Namsan Tower")
				.participants(2)
				.startDate(LocalDate.now())
				.endDate(LocalDate.now().plusDays(3))
				.content("서울 투어")
				.build()
		);

		Page<Travel> travelPage = new PageImpl<>(mockTravelList, pageable, mockTravelList.size());

		when(travelRepository.searchTravels(null, pageable)).thenReturn(travelPage);

		// When
		Page<TravelDto> result = travelService.getTravels(null, pageable);

		// Then
		assertNotNull(result);
		assertEquals(2, result.getTotalElements());
		verify(travelRepository, times(1)).searchTravels(null, pageable);
	}

	@Test
	@DisplayName("✅ 정상 테스트: 특정 카테고리에 대한 여행 목록 조회")
	void testGetTravels_WithCategory() {
		// Given
		Long categoryId = 1L;
		Pageable pageable = PageRequest.of(0, 5, Sort.by("createdAt").descending());
		List<Travel> mockTravelList = Collections.singletonList(
			Travel.builder()
				.user(new Member("힐링", "asd@naver.com", "testPassword", "John Doe", null))
				.category(new TravelCategory("힐링"))
				.city("Paris")
				.places("Eiffel Tower, Louvre Museum")
				.participants(3)
				.startDate(LocalDate.now())
				.endDate(LocalDate.now().plusDays(5))
				.content("파리 야경 여행")
				.build()
		);
		Page<Travel> travelPage = new PageImpl<>(mockTravelList, pageable, mockTravelList.size());

		when(travelRepository.searchTravels(categoryId, pageable)).thenReturn(travelPage);

		// When
		Page<TravelDto> result = travelService.getTravels(categoryId, pageable);

		// Then
		assertNotNull(result);
		assertEquals(1, result.getTotalElements());
		verify(travelRepository, times(1)).searchTravels(categoryId, pageable);
	}

	@Test
	@DisplayName("✅ 예외 테스트: 검색된 여행이 없는 경우")
	void testGetTravels_EmptyList() {
		// Given
		Long categoryId = 99L;
		Pageable pageable = PageRequest.of(0, 5, Sort.by("createdAt").descending());
		Page<Travel> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

		when(travelRepository.searchTravels(categoryId, pageable)).thenReturn(emptyPage);

		// When
		Page<TravelDto> result = travelService.getTravels(categoryId, pageable);

		// Then
		assertNotNull(result);
		assertEquals(0, result.getTotalElements());
		verify(travelRepository, times(1)).searchTravels(categoryId, pageable);
	}

	@Test
	@DisplayName("✅ 정상 테스트: 여행 상세 조회 성공")
	void testGetTravelDetail_Success() {
		// Given
		Long travelId = 1L;
		LocalDateTime now = LocalDateTime.now();

		Travel mockTravel = Travel.builder()
			.user(new Member("힐링", "asd@naver.com", "testPassword", "John Doe", null))
			.category(new TravelCategory("힐링"))
			.city("Paris")
			.places("Eiffel Tower, Louvre Museum")
			.participants(3)
			.startDate(LocalDate.of(2025, 6, 1))
			.endDate(LocalDate.of(2025, 6, 7))
			.content("파리 여행을 계획 중입니다.")
			.status(TravelStatus.WAITING_FOR_MATCHING)
			.build();

		ReflectionTestUtils.setField(mockTravel, "createdAt", now);
		ReflectionTestUtils.setField(mockTravel, "updatedAt", now);

		when(travelRepository.findById(travelId)).thenReturn(Optional.of(mockTravel));

		// When
		TravelDto response = travelService.getTravelDetail(travelId);

		// Then
		assertNotNull(response);
		assertEquals(mockTravel.getCity(), response.getCity());
		assertEquals(mockTravel.getPlaces(), response.getPlaces());
		assertEquals(mockTravel.getParticipants(), response.getParticipants());
		assertEquals(mockTravel.getContent(), response.getContent());
		assertEquals(mockTravel.getCategory().getName(), response.getCategoryName());

		verify(travelRepository, times(1)).findById(travelId);
	}

	@Test
	@DisplayName("❌ 예외 테스트: 존재하지 않는 여행 ID")
	void testGetTravelDetail_NotFound() {
		// Given
		Long invalidTravelId = 999L;

		when(travelRepository.findById(invalidTravelId))
			.thenReturn(Optional.empty());

		// When & Then
		CustomException exception = assertThrows(CustomException.class, () -> {
			travelService.getTravelDetail(invalidTravelId);
		});

		assertEquals(ErrorCode.TRAVEL_NOT_FOUND, exception.getErrorCode());
		verify(travelRepository, times(1)).findById(invalidTravelId);
	}
}

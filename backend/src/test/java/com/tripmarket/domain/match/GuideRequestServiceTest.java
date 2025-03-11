// package com.example.backend.domain.match;
//
// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.Mockito.*;
//
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
//
// import com.tripmarket.domain.guide.entity.Guide;
// import com.tripmarket.domain.guide.service.GuideService;
// import com.tripmarket.domain.match.dto.request.GuideRequestCreate;
// import com.tripmarket.domain.match.entity.GuideRequest;
// import com.tripmarket.domain.match.repository.GuideRequestRepository;
// import com.tripmarket.domain.match.service.GuideRequestService;
// import com.tripmarket.domain.member.entity.Member;
// import com.tripmarket.domain.member.service.MemberService;
// import com.tripmarket.domain.travel.entity.Travel;
// import com.tripmarket.domain.travel.service.TravelService;
// import com.tripmarket.global.exception.CustomException;
// import com.tripmarket.global.exception.ErrorCode;
//
// @ExtendWith(MockitoExtension.class)
// class GuideRequestServiceTest {
//
// 	@Mock
// 	private GuideRequestRepository guideRequestRepository;
//
// 	@Mock
// 	private MemberService memberService;
//
// 	@Mock
// 	private GuideService guideService;
//
// 	@Mock
// 	private TravelService travelService;
//
// 	@InjectMocks
// 	private GuideRequestService guideRequestService;
//
// 	@Test
// 	@DisplayName("✅ 정상 테스트: 사용자가 가이더에게 여행 요청을 보냄")
// 	void testCreateGuideRequest_Success() {
// 		// Given
// 		Long userId = 1L;
// 		Long guideId = 2L;
// 		Long travelId = 10L;
//
// 		Member mockMember = mock(Member.class);
// 		Guide mockGuide = mock(Guide.class);
// 		Travel mockTravel = mock(Travel.class);
// 		GuideRequestCreate request = GuideRequestCreate.builder()
// 			.travelId(travelId)
// 			.build();
//
// 		when(memberService.getMemberById(userId)).thenReturn(mockMember);
// 		when(guideService.getGuide(guideId)).thenReturn(mockGuide);
// 		when(guideRequestRepository.existsByMemberIdAndGuideIdAndTravelId(userId, guideId, travelId))
// 			.thenReturn(false);
// 		when(travelService.getTravel(travelId)).thenReturn(mockTravel);
//
// 		// When
// 		guideRequestService.createGuideRequest(userId, guideId, request);
//
// 		// Then
// 		verify(guideRequestRepository, times(1)).save(any(GuideRequest.class));
// 	}
//
// 	@Test
// 	@DisplayName("❌ 예외 테스트: 이미 동일한 가이더에게 요청을 보낸 경우")
// 	void testCreateGuideRequest_DuplicateRequest() {
// 		// Given
// 		Long userId = 1L;
// 		Long guideId = 2L;
// 		Long travelId = 10L;
//
// 		GuideRequestCreate request = GuideRequestCreate.builder()
// 			.travelId(travelId)
// 			.build();
//
// 		when(guideRequestRepository.existsByMemberIdAndGuideIdAndTravelId(userId, guideId, travelId))
// 			.thenReturn(true);
//
// 		// When & Then
// 		CustomException exception = assertThrows(CustomException.class, () -> {
// 			guideRequestService.createGuideRequest(userId, guideId, request);
// 		});
//
// 		assertEquals(ErrorCode.DUPLICATE_REQUEST, exception.getErrorCode());
// 	}
//
// 	@Test
// 	@DisplayName("❌ 예외 테스트: 존재하지 않는 여행 ID")
// 	void testCreateGuideRequest_TravelNotFound() {
// 		// Given
// 		Long userId = 1L;
// 		Long guideId = 2L;
// 		Long travelId = 999L; // 유효하지 않은 여행 ID
//
// 		GuideRequestCreate request = GuideRequestCreate.builder()
// 			.travelId(travelId)
// 			.build();
//
// 		when(travelService.getTravel(travelId)).thenThrow(new CustomException(ErrorCode.TRAVEL_NOT_FOUND));
//
// 		// When & Then
// 		CustomException exception = assertThrows(CustomException.class, () -> {
// 			guideRequestService.createGuideRequest(userId, guideId, request);
// 		});
//
// 		assertEquals(ErrorCode.TRAVEL_NOT_FOUND, exception.getErrorCode());
// 	}
// }

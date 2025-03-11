package com.tripmarket.domain.guide;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripmarket.domain.guide.dto.GuideCreateRequest;
import com.tripmarket.domain.guide.service.GuideService;
import com.tripmarket.global.oauth2.CustomOAuth2User;

@SpringBootTest
@AutoConfigureMockMvc
class GuideControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private GuideService guideService;

	private static final ResultMatcher DEFAULT_GLOBAL_ERROR_CODE = status().isInternalServerError();

	private final CustomOAuth2User testUser = new CustomOAuth2User(
		Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),
		Map.of("id", 1L, "email", "test@example.com"),
		"id",
		1L,
		"test@example.com"
	);

	private final Authentication authentication = new TestingAuthenticationToken(
		testUser, null, List.of(new SimpleGrantedAuthority("ROLE_USER"))
	);

	private static Stream<Arguments> provideGuideCreationTestCases() {
		return Stream.of(
			Arguments.of(null, DEFAULT_GLOBAL_ERROR_CODE),  // 언어가 null
			Arguments.of("", DEFAULT_GLOBAL_ERROR_CODE),  // 언어가 비어있을 때 오류 반환
			Arguments.of("지원하지않는언어", DEFAULT_GLOBAL_ERROR_CODE), // 지원하지 않는 언어 오류
			Arguments.of("한국어, 지원하지않는언어", DEFAULT_GLOBAL_ERROR_CODE), // 일부 언어만 지원할 경우 오류
			Arguments.of("한국어", status().isCreated()), // 단일 언어 지원
			Arguments.of("한국어, 영어", status().isCreated()) // 여러 언어 지원
		);
	}

	@ParameterizedTest
	@MethodSource("provideGuideCreationTestCases")
	@DisplayName("가이드 생성 API 언어 검증 테스트")
	void languageValidationParameterizedTest(String languages, ResultMatcher expectedStatus) throws Exception {
		performGuideCreateRequest(languages, expectedStatus);
	}

	// 테스트 수행 메서드
	private void performGuideCreateRequest(String languages, ResultMatcher expectedStatus) throws Exception {
		GuideCreateRequest guideCreateRequest = GuideCreateRequest.builder()
			.name("test1")
			.activityRegion("test1")
			.introduction("test1")
			.languages(languages)
			.build();

		doNothing().when(guideService).create(Mockito.any(), Mockito.any());

		mockMvc.perform(post("/guides")
				.with(authentication(authentication))
				.content(asJsonString(guideCreateRequest))
				.contentType("application/json"))
			.andExpect(expectedStatus);
	}

	// JSON 변환 메서드
	private static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}

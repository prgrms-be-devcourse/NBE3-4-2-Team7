package com.example.backend.domain.guide;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripmarket.domain.guide.controller.GuideController;
import com.tripmarket.domain.guide.dto.GuideDto;
import com.tripmarket.domain.guide.entity.LanguagesValidator;
import com.tripmarket.domain.guide.service.GuideService;
import com.tripmarket.domain.guide.service.GuideValidationService;

@WebMvcTest(controllers = GuideController.class)
@Import({LanguagesValidator.class})
class GuideControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private GuideService guideService;

	@MockitoBean
	private GuideValidationService guideValidationService;

	@Test
	@DisplayName("가이드 생성 시 Languages 가 비어있으면 오류 반환을 해야한다.")
	void createGuideExceptionTest1() throws Exception {
		// String emptyLanguages = "";
		//
		// GuideDto guideDto = GuideDto.builder()
		// 	.name("test1")
		// 	.activityRegion("test1")
		// 	.introduction("test1")
		// 	.languages(emptyLanguages)
		// 	.build();
		//
		// doNothing().when(guideService).create(any());
		//
		// mockMvc.perform(post("/guides")
		// 		.content(asJsonString(guideDto))
		// 		.contentType(MediaType.APPLICATION_JSON))
		// 	.andExpect(status().isInternalServerError());
		// //.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("가이드 생성 시 지원하지 않는 Languages 면 오류 반환을 해야한다.")
	void createGuideExceptionTest2() throws Exception {
		// TODO : 테스팅 환경에서 커스텀 배릴데이터 작동 방법 찾기

		// String notSupportedLanguages = "some_languages";
		//
		// GuideDto guideDto = GuideDto.builder()
		// 	.name("test2")
		// 	.activityRegion("test2")
		// 	.introduction("test2")
		// 	.languages(notSupportedLanguages)
		// 	.build();
		//
		// doNothing().when(guideService).create(any());
		//
		// mockMvc.perform(post("/guides")
		// 		.content(asJsonString(guideDto))
		// 		.contentType(MediaType.APPLICATION_JSON))
		// 	.andExpect(status().isBadRequest());

	}

	// to json string
	private static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
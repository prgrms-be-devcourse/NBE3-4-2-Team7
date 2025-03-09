package com.tripmarket.domain.member.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripmarket.domain.member.entity.Member;
import com.tripmarket.domain.member.repository.MemberRepository;
import com.tripmarket.global.oauth2.CustomOAuth2User;
import com.tripmarket.global.security.CustomUserDetails;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class MemberControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MemberRepository memberRepository;

	// 테스트 상수
	private static final String LOCAL_USER_EMAIL = "test@test.com";
	private static final String KAKAO_USER_EMAIL = "kakao@test.com";
	private static final String GOOGLE_USER_EMAIL = "google@test.com";
	private static final String GITHUB_USER_EMAIL = "github@test.com";

	private static final Long LOCAL_USER_ID = 1L;
	private static final Long KAKAO_USER_ID = 2L;
	private static final Long GOOGLE_USER_ID = 3L;
	private static final Long GITHUB_USER_ID = 4L;

	/**
	 * 테스트용 인증 처리기 생성 (일반 로그인)
	 */
	private RequestPostProcessor localUserLogin(Long userId) {
		Member member = memberRepository.findById(userId).orElseThrow();
		CustomUserDetails userDetails = new CustomUserDetails(member);
		return SecurityMockMvcRequestPostProcessors.user(userDetails);
	}

	/**
	 * 테스트용 인증 처리기 생성 (소셜 로그인)
	 */
	private RequestPostProcessor socialUserLogin(Long userId) {
		Member member = memberRepository.findById(userId).orElseThrow();

		// OAuth2 속성 맵 생성
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("id", member.getProviderId());
		attributes.put("email", member.getEmail());
		attributes.put("name", member.getName());

		// CustomOAuth2User 생성
		CustomOAuth2User oAuth2User = new CustomOAuth2User(
			Collections.singletonList(new SimpleGrantedAuthority(member.getRole().name())),
			attributes,
			"email",
			member.getId(),
			member.getEmail()
		);

		// OAuth2 인증 객체 생성
		OAuth2AuthenticationToken authenticationToken = new OAuth2AuthenticationToken(
			oAuth2User,
			oAuth2User.getAuthorities(),
			member.getProvider().name().toLowerCase()
		);

		return SecurityMockMvcRequestPostProcessors.authentication(authenticationToken);
	}

	@Test
	@DisplayName("내 정보 조회 성공 - 로컬")
	@WithUserDetails(value = "test@test.com", userDetailsServiceBeanName = "customUserDetailsService")
	void getMyInfo_success_local() throws Exception {
		// when & then
		mockMvc.perform(get("/members/me")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(LOCAL_USER_ID))
			.andExpect(jsonPath("$.email").value(LOCAL_USER_EMAIL))
			.andExpect(jsonPath("$.name").value("테스트유저"))
			.andExpect(jsonPath("$.hasGuideProfile").value(false));
	}

	@Test
	@DisplayName("내 정보 조회 성공 - 카카오")
	void getMyInfo_success_kakao() throws Exception {
		// when & then
		mockMvc.perform(get("/members/me")
				.with(socialUserLogin(KAKAO_USER_ID))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(KAKAO_USER_ID))
			.andExpect(jsonPath("$.email").value(KAKAO_USER_EMAIL))
			.andExpect(jsonPath("$.name").value("카카오사용자"))
			.andExpect(jsonPath("$.hasGuideProfile").value(false));
	}

	@Test
	@DisplayName("내 정보 조회 성공 - 구글")
	void getMyInfo_success_google() throws Exception {
		// when & then
		mockMvc.perform(get("/members/me")
				.with(socialUserLogin(GOOGLE_USER_ID))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(GOOGLE_USER_ID))
			.andExpect(jsonPath("$.email").value(GOOGLE_USER_EMAIL))
			.andExpect(jsonPath("$.name").value("구글사용자"))
			.andExpect(jsonPath("$.hasGuideProfile").value(false));
	}

	@Test
	@DisplayName("내 정보 조회 성공 - 깃허브")
	void getMyInfo_success_github() throws Exception {
		// when & then
		mockMvc.perform(get("/members/me")
				.with(socialUserLogin(GITHUB_USER_ID))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(GITHUB_USER_ID))
			.andExpect(jsonPath("$.email").value(GITHUB_USER_EMAIL))
			.andExpect(jsonPath("$.name").value("깃허브사용자"))
			.andExpect(jsonPath("$.hasGuideProfile").value(false));
	}

	@Test
	@DisplayName("가이드 프로필 존재 여부 확인 - 로컬")
	void hasGuideProfile_local() throws Exception {
		// when & then
		mockMvc.perform(get("/members/me/guide")
				.with(localUserLogin(LOCAL_USER_ID))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().string("false"));
	}

	@Test
	@DisplayName("가이드 프로필 존재 여부 확인 - 소셜")
	void hasGuideProfile_social() throws Exception {
		// when & then
		mockMvc.perform(get("/members/me/guide")
				.with(socialUserLogin(KAKAO_USER_ID))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().string("false"));
	}

	@Test
	@DisplayName("인증되지 않은 사용자 접근 거부")
	void unauthenticatedAccess() throws Exception {
		// when & then
		mockMvc.perform(get("/members/me")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnauthorized());
	}

	@Test
	@DisplayName("내 여행 목록 조회 - 데이터 없음 (로컬)")
	void getMyTravels_emptyList_local() throws Exception {
		// when & then
		mockMvc.perform(get("/members/me/travels")
				.with(localUserLogin(LOCAL_USER_ID))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isArray())
			.andExpect(jsonPath("$.length()").value(0));
	}

	@Test
	@DisplayName("내 여행 목록 조회 - 데이터 없음 (소셜)")
	void getMyTravels_emptyList_socialLogin() throws Exception {
		// when & then
		mockMvc.perform(get("/members/me/travels")
				.with(socialUserLogin(GOOGLE_USER_ID))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isArray())
			.andExpect(jsonPath("$.length()").value(0));
	}
}
package com.tripmarket.domain.auth.controller;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripmarket.domain.auth.dto.LoginRequestDto;
import com.tripmarket.domain.auth.dto.SignupRequestDto;
import com.tripmarket.domain.member.repository.MemberRepository;

import jakarta.servlet.http.Cookie;

@SpringBootTest(classes = com.tripmarket.BackendApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MemberRepository memberRepository;

	// 테스트 상수
	private static final String TEST_EMAIL = "test@test.com";
	private static final String TEST_PASSWORD = "password123!";
	private static final String ADMIN_EMAIL = "admin@test.com";
	private static final String NEW_USER_EMAIL = "newuser@test.com";

	@Test
	@DisplayName("회원가입 성공")
	void signUp_success() throws Exception {
		// given
		SignupRequestDto signupRequestDto = new SignupRequestDto(
			NEW_USER_EMAIL,
			"새사용자",
			TEST_PASSWORD,
			null
		);

		// when & then
		mockMvc.perform(post("/auth/signup")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(signupRequestDto)))
			.andExpect(status().isCreated());

		// 데이터베이스에 사용자가 생성되었는지 확인
		assertThat(memberRepository.findByEmail(NEW_USER_EMAIL)).isPresent();
	}

	@Test
	@DisplayName("회원가입 실패 - 이메일 중복")
	void signUp_fail_duplicateEmail() throws Exception {
		// given - 이미 data-test.sql에 존재하는 이메일 사용
		SignupRequestDto signupRequestDto = new SignupRequestDto(
			TEST_EMAIL, // 이미 존재하는 이메일
			"중복사용자",
			"password123!",
			null
		);

		// when & then
		mockMvc.perform(post("/auth/signup")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(signupRequestDto)))
			.andExpect(status().isConflict()); // 409 Conflict
	}

	@Test
	@DisplayName("회원가입 실패 - 유효성 검사")
	void signUp_fail_validation() throws Exception {
		// given - 잘못된 이메일 형식
		SignupRequestDto signupRequestDto = new SignupRequestDto(
			"invalid-email",
			"유효하지않은사용자",
			"pwd", // 짧은 비밀번호
			null
		);

		// when & then
		mockMvc.perform(post("/auth/signup")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(signupRequestDto)))
			.andExpect(status().isBadRequest()); // 400 Bad Request
	}

	@Test
	@DisplayName("로그인 성공")
	void login_success() throws Exception {
		// given
		LoginRequestDto loginRequestDto = new LoginRequestDto(TEST_EMAIL, TEST_PASSWORD);

		// when & then
		mockMvc.perform(post("/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginRequestDto)))
			.andExpect(status().isOk())
			.andExpect(header().exists(HttpHeaders.SET_COOKIE))
			.andExpect(cookie().exists("accessToken"))
			.andExpect(cookie().exists("refreshToken"));
	}

	@Test
	@DisplayName("로그인 실패 - 잘못된 비밀번호")
	void login_fail_wrongPassword() throws Exception {
		// given
		LoginRequestDto loginRequestDto = new LoginRequestDto(TEST_EMAIL, "wrongpassword");

		// when & then
		mockMvc.perform(post("/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginRequestDto)))
			.andExpect(status().isUnauthorized()); // 401 Unauthorized
	}

	@Test
	@DisplayName("로그인 실패 - 존재하지 않는 사용자")
	void login_fail_userNotFound() throws Exception {
		// given
		LoginRequestDto loginRequestDto = new LoginRequestDto("wrong@test.com", TEST_PASSWORD);

		// when & then
		mockMvc.perform(post("/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginRequestDto)))
			.andExpect(status().isUnauthorized()); // 401 Unauthorized
	}

	@Test
	@DisplayName("토큰 갱신 성공")
	void refreshToken_success() throws Exception {
		// given - 먼저 로그인하여 토큰 얻기
		LoginRequestDto loginRequestDto = new LoginRequestDto(TEST_EMAIL, TEST_PASSWORD);

		MvcResult loginResult = mockMvc.perform(post("/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginRequestDto)))
			.andExpect(status().isOk())
			.andReturn();

		// 쿠키 추출
		Cookie[] cookies = loginResult.getResponse().getCookies();

		// when & then
		mockMvc.perform(post("/auth/refresh")
				.cookie(cookies)) // 로그인에서 얻은 쿠키 사용
			.andExpect(status().isOk())
			.andExpect(header().exists(HttpHeaders.SET_COOKIE))
			.andExpect(cookie().exists("accessToken"));
	}

	@Test
	@DisplayName("토큰 갱신 실패 - 유효하지 않은 리프레시 토큰")
	void refreshToken_fail_invalidToken() throws Exception {
		// given - 유효하지 않은 리프레시 토큰
		Cookie invalidRefreshToken = new Cookie("refreshToken", "wrongRefreshToken");

		// when & then
		mockMvc.perform(post("/auth/refresh")
				.cookie(invalidRefreshToken))
			.andExpect(status().isUnauthorized()); // 401 Unauthorized
	}

	@Test
	@DisplayName("로그아웃 성공")
	void logout_success() throws Exception {
		// given - 먼저 로그인하여 토큰 얻기
		LoginRequestDto loginRequestDto = new LoginRequestDto(TEST_EMAIL, TEST_PASSWORD);

		MvcResult loginResult = mockMvc.perform(post("/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginRequestDto)))
			.andExpect(status().isOk())
			.andReturn();

		// 쿠키 추출
		Cookie[] cookies = loginResult.getResponse().getCookies();

		// when & then
		mockMvc.perform(post("/auth/logout")
				.cookie(cookies)) // 로그인에서 얻은 쿠키 사용
			.andExpect(status().isOk())
			.andExpect(header().exists(HttpHeaders.SET_COOKIE))
			.andExpect(cookie().maxAge("accessToken", 0))
			.andExpect(cookie().maxAge("refreshToken", 0));
	}

	@Test
	@DisplayName("로그인-토큰갱신-로그아웃 통합 시나리오")
	void login_refresh_logout() throws Exception {
		// 1. 로그인
		LoginRequestDto loginRequestDto = new LoginRequestDto(TEST_EMAIL, TEST_PASSWORD);

		MvcResult loginResult = mockMvc.perform(post("/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginRequestDto)))
			.andExpect(status().isOk())
			.andExpect(cookie().exists("accessToken"))
			.andExpect(cookie().exists("refreshToken"))
			.andReturn();

		Cookie[] loginCookies = loginResult.getResponse().getCookies();

		// 2. 토큰 갱신
		MvcResult refreshResult = mockMvc.perform(post("/auth/refresh")
				.cookie(loginCookies))
			.andExpect(status().isOk())
			.andExpect(cookie().exists("accessToken"))
			.andReturn();

		Cookie[] refreshedCookies = refreshResult.getResponse().getCookies();

		// 3. 로그아웃
		mockMvc.perform(post("/auth/logout")
				.cookie(refreshedCookies))
			.andExpect(status().isOk())
			.andExpect(cookie().maxAge("accessToken", 0))
			.andExpect(cookie().maxAge("refreshToken", 0));

		// 4. 로그아웃 후 토큰 갱신 시도 (실패해야 함)
		mockMvc.perform(post("/auth/refresh")
				.cookie(refreshedCookies))
			.andExpect(status().isUnauthorized());
	}
}
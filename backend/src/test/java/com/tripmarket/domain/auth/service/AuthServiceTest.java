package com.tripmarket.domain.auth.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.tripmarket.domain.auth.dto.LoginRequestDto;
import com.tripmarket.domain.auth.dto.SignupRequestDto;
import com.tripmarket.domain.member.entity.Member;
import com.tripmarket.domain.member.entity.Provider;
import com.tripmarket.domain.member.repository.MemberRepository;
import com.tripmarket.global.exception.CustomException;
import com.tripmarket.global.exception.ErrorCode;
import com.tripmarket.global.jwt.JwtTokenProvider;
import com.tripmarket.global.security.CustomUserDetails;
import com.tripmarket.global.util.CookieUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

	@Mock
	private AuthenticationManager authenticationManager;

	@Mock
	private JwtTokenProvider jwtTokenProvider;

	@Mock
	private RedisTemplate<String, String> redisTemplate;

	@Mock
	private ValueOperations<String, String> valueOperations;

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private CookieUtil cookieUtil;

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@InjectMocks
	private AuthService authService;

	private Member testMember;

	private Authentication authentication;

	private CustomUserDetails customUserDetails;

	@BeforeEach
	void setUp() {
		// 테스트용 멤버 생성
		testMember = Member.builder()
			.email("test@test.com")
			.password("password")
			.name("테스트유저")
			.provider(Provider.LOCAL)
			.providerId(null)
			.imageUrl(null)
			.build();

		ReflectionTestUtils.setField(testMember, "id", 1L);

		customUserDetails = new CustomUserDetails(testMember);

		authentication = new UsernamePasswordAuthenticationToken(
			customUserDetails,
			null,
			Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
		);
	}

	@Test
	@DisplayName("회원가입 성공")
	void signup_success() {
		// given
		SignupRequestDto signUpRequestDto = new SignupRequestDto(
			"signup@test.com",
			"신규유저",
			"password1234",
			null
		);

		when(memberRepository.findByEmail(signUpRequestDto.email())).thenReturn(Optional.empty());
		when(passwordEncoder.encode(signUpRequestDto.password())).thenReturn("encodedPassword");

		// Member 저장 시 반환될 객체를 캡처하기 위한 ArgumentCaptor 설정
		ArgumentCaptor<Member> memberCaptor = ArgumentCaptor.forClass(Member.class);

		// when
		authService.signUp(signUpRequestDto);

		// then
		verify(memberRepository).findByEmail(signUpRequestDto.email());
		verify(passwordEncoder).encode(signUpRequestDto.password());
		verify(memberRepository).save(memberCaptor.capture());

		Member savedMember = memberCaptor.getValue();
		assertThat(savedMember.getEmail()).isEqualTo("signup@test.com");
		assertThat(savedMember.getPassword()).isEqualTo("encodedPassword");
		assertThat(savedMember.getProvider()).isEqualTo(Provider.LOCAL);
	}

	@Test
	@DisplayName("회원가입 실패 - 이메일 중복")
	void signup_fail() {
		// given
		SignupRequestDto signUpRequestDto = new SignupRequestDto(
			"test@test.com",
			"password1234",
			"중복유저",
			null
		);

		when(memberRepository.findByEmail(signUpRequestDto.email())).thenReturn(Optional.of(testMember));

		// when & then
		assertThatThrownBy(() -> authService.signUp(signUpRequestDto))
			.isInstanceOf(CustomException.class)
			.satisfies(exception -> {
				CustomException customException = (CustomException)exception;
				assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.DUPLICATE_EMAIL);
			});
	}

	@Test
	@DisplayName("로그인 성공")
	void login_success() {
		// given
		String email = "test@test.com";
		String password = "password";

		LoginRequestDto loginRequestDto = new LoginRequestDto(email, password);

		// 특정 이메일/비밀번호 조합에 대해서만 성공하도록 모킹
		when(authenticationManager.authenticate(
			argThat(auth ->
				auth.getName().equals(email) &&
					auth.getCredentials().equals(password)
			)
		)).thenReturn(authentication);

		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(jwtTokenProvider.createAccessToken(authentication)).thenReturn("accessTokenValue");
		when(jwtTokenProvider.createRefreshToken(testMember.getId())).thenReturn("refreshTokenValue");

		// when
		Map<String, String> tokens = authService.login(loginRequestDto);

		// then
		assertThat(tokens).containsKeys("accessToken", "refreshToken");
		assertThat(tokens.get("accessToken")).isEqualTo("accessTokenValue");
		assertThat(tokens.get("refreshToken")).isEqualTo("refreshTokenValue");

		verify(authenticationManager).authenticate(
			argThat(auth ->
				auth.getName().equals(email) &&
					auth.getCredentials().equals(password)
			)
		);
		verify(jwtTokenProvider).createAccessToken(authentication);
		verify(jwtTokenProvider).createRefreshToken(testMember.getId());
		verify(redisTemplate.opsForValue())
			.set(
				eq("RT:" + testMember.getId()),
				eq("refreshTokenValue"),
				eq(7L),
				any()
			);
	}

	@Test
	@DisplayName("로그인 실패 - 잘못된 이메일")
	void login_fail_invalid_email() {
		// given
		String wrongEmail = "wrong@test.com";
		String password = "password";

		LoginRequestDto loginRequestDto = new LoginRequestDto(wrongEmail, password);

		when(authenticationManager.authenticate(
			argThat(auth ->
				auth.getName().equals(wrongEmail) &&
					auth.getCredentials().equals(password)
			)
		)).thenThrow(new BadCredentialsException("잘못된 이메일 또는 비밀번호"));

		// when & then
		assertThatThrownBy(() -> authService.login(loginRequestDto))
			.isInstanceOf(BadCredentialsException.class);
	}

	@Test
	@DisplayName("토큰 갱신 성공")
	void refreshToken_success() {
		// given
		String refreshToken = "refreshTokenValue";

		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(jwtTokenProvider.validateToken(refreshToken)).thenReturn(true);
		when(jwtTokenProvider.getUserIdFromRefreshToken(refreshToken)).thenReturn(1L);
		when(redisTemplate.opsForValue().get("RT:1")).thenReturn(refreshToken);
		when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));
		when(jwtTokenProvider.createAccessToken(any(Authentication.class))).thenReturn("newAccessToken");

		// when
		String newAccessToken = authService.refreshToken(refreshToken, request, response);

		// then
		assertThat(newAccessToken).isEqualTo("newAccessToken");

		verify(jwtTokenProvider).validateToken(refreshToken);
		verify(jwtTokenProvider).getUserIdFromRefreshToken(refreshToken);
		verify(redisTemplate.opsForValue()).get("RT:1");
		verify(memberRepository).findById(1L);
		verify(jwtTokenProvider).createAccessToken(any(Authentication.class));
	}

	@Test
	@DisplayName("토큰 갱신 실패 - 유효하지 않은 리프레시 토큰")
	void refreshToken_invalidToken() {
		// given
		String refreshToken = "RefreshTokenValue";

		when(jwtTokenProvider.validateToken(refreshToken)).thenReturn(false);

		// when & then
		assertThatThrownBy(() -> authService.refreshToken(refreshToken, request, response))
			.isInstanceOf(CustomException.class)
			.satisfies(exception -> {
				CustomException customException = (CustomException)exception;
				assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.TOKEN_REFRESH_FAILED);
			});

		verify(jwtTokenProvider).validateToken(refreshToken);
	}

	@Test
	@DisplayName("토큰 갱신 실패 - Redis에 저장된 토큰과 불일치")
	void refreshToken_tokenMismatch() {
		// given
		String refreshToken = "wrongRefreshTokenValue";
		String storedToken = "refreshTokenValue";

		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(jwtTokenProvider.validateToken(refreshToken)).thenReturn(true);
		when(jwtTokenProvider.getUserIdFromRefreshToken(refreshToken)).thenReturn(1L);
		when(redisTemplate.opsForValue().get("RT:1")).thenReturn(storedToken);

		// when & then
		assertThatThrownBy(() -> authService.refreshToken(refreshToken, request, response))
			.isInstanceOf(CustomException.class)
			.satisfies(exception -> {
				CustomException customException = (CustomException)exception;
				assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.INVALID_REFRESH_TOKEN);
			});

		verify(jwtTokenProvider).validateToken(refreshToken);
		verify(jwtTokenProvider).getUserIdFromRefreshToken(refreshToken);
		verify(redisTemplate.opsForValue()).get("RT:1");
	}

	@Test
	@DisplayName("로그아웃 성공")
	void logout_success() {
		// given
		String accessToken = "accessTokenValue";
		String refreshToken = "refreshTokenValue";

		when(cookieUtil.extractAccessTokenFromCookie(request)).thenReturn(accessToken);
		when(cookieUtil.extractRefreshTokenFromCookie(request)).thenReturn(refreshToken);
		when(jwtTokenProvider.getUserIdFromRefreshToken(refreshToken)).thenReturn(1L);
		when(jwtTokenProvider.validateToken(accessToken)).thenReturn(true);
		when(redisTemplate.delete("RT:1")).thenReturn(true);

		ResponseCookie accessCookie = ResponseCookie.from("accessToken", "").maxAge(0).path("/").build();
		ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", "").maxAge(0).path("/").build();

		when(cookieUtil.createLogoutAccessCookie()).thenReturn(accessCookie);
		when(cookieUtil.createLogoutRefreshCookie()).thenReturn(refreshCookie);

		// when
		authService.logout(request, response);

		// then
		verify(cookieUtil).extractAccessTokenFromCookie(request);
		verify(cookieUtil).extractRefreshTokenFromCookie(request);
		verify(jwtTokenProvider).getUserIdFromRefreshToken(refreshToken);
		verify(jwtTokenProvider).validateToken(accessToken);
		verify(jwtTokenProvider).addToBlacklist(accessToken);
		verify(redisTemplate).delete("RT:1");
		verify(cookieUtil).createLogoutAccessCookie();
		verify(cookieUtil).createLogoutRefreshCookie();
		verify(response, times(2)).addHeader(eq(HttpHeaders.SET_COOKIE), anyString());
	}
}
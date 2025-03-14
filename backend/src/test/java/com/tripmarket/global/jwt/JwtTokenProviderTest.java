package com.tripmarket.global.jwt;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import com.tripmarket.domain.member.entity.Member;
import com.tripmarket.domain.member.entity.Provider;
import com.tripmarket.domain.member.repository.MemberRepository;
import com.tripmarket.global.auth.AuthenticatedUser;
import com.tripmarket.global.exception.CustomException;
import com.tripmarket.global.exception.ErrorCode;
import com.tripmarket.global.oauth2.CustomOAuth2User;
import com.tripmarket.global.security.CustomUserDetails;

@ExtendWith(MockitoExtension.class)
public class JwtTokenProviderTest {

	@Mock
	private RedisTemplate<String, String> redisTemplate;

	@Mock
	private MemberRepository memberRepository;

	@InjectMocks
	private JwtTokenProvider jwtTokenProvider;

	@BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(jwtTokenProvider, "secretKey", "testSecretKey12345678901234567890");
		ReflectionTestUtils.setField(jwtTokenProvider, "accessTokenValidityInSeconds", 3600L);
		ReflectionTestUtils.setField(jwtTokenProvider, "refreshTokenValidityInSeconds", 86400L);
		jwtTokenProvider.init();
	}

	private Authentication createMockOAuth2Authentication() {
		Collection<GrantedAuthority> authorities =
			Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));

		Map<String, Object> attributes = new HashMap<>();
		attributes.put("id", 1L);
		attributes.put("email", "social@test.com");

		CustomOAuth2User principal = new CustomOAuth2User(
			authorities,
			attributes,
			"id",
			1L,
			"social@test.com"
		);

		return new UsernamePasswordAuthenticationToken(principal, "", authorities);
	}

	private Authentication createMockUserDetailsAuthentication() {
		Collection<GrantedAuthority> authorities =
			Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));

		Member member = Member.builder()
			.email("local@test.com")
			.name("테스트")
			.provider(Provider.LOCAL)
			.build();

		// Reflection을 사용하여 id 설정
		ReflectionTestUtils.setField(member, "id", 1L);

		CustomUserDetails principal = new CustomUserDetails(member);
		return new UsernamePasswordAuthenticationToken(principal, "", authorities);
	}

	@Test
	@DisplayName("엑세스 토큰 생성 및 검증 - 소셜")
	void createAccessToken_social() {
		// given
		Authentication auth = createMockOAuth2Authentication();

		// when
		String token = jwtTokenProvider.createAccessToken(auth);

		// then
		assertThat(token).isNotNull();
		assertThat(jwtTokenProvider.validateToken(token)).isTrue();

		// 토큰에서 추출한 인증 정보 검증
		Authentication resultAuth = jwtTokenProvider.getAuthentication(token);
		AuthenticatedUser principal = (AuthenticatedUser)resultAuth.getPrincipal();

		assertThat(principal.getId()).isEqualTo(1L);
		assertThat(principal.getEmail()).isEqualTo("social@test.com");
		assertThat(resultAuth.getAuthorities())
			.extracting("authority")
			.containsExactlyInAnyOrder("ROLE_USER");
	}

	@Test
	@DisplayName("엑세스 토큰 생성 및 검증 - 로컬")
	void createAccessToken_local() {
		// given
		Authentication auth = createMockUserDetailsAuthentication();

		// when
		String token = jwtTokenProvider.createAccessToken(auth);

		// then
		assertThat(token).isNotNull();
		assertThat(jwtTokenProvider.validateToken(token)).isTrue();

		// 토큰에서 추출한 인증 정보 검증
		Authentication resultAuth = jwtTokenProvider.getAuthentication(token);
		AuthenticatedUser principal = (AuthenticatedUser)resultAuth.getPrincipal();

		assertThat(principal.getId()).isEqualTo(1L);
		assertThat(principal.getEmail()).isEqualTo("local@test.com");
		assertThat(resultAuth.getAuthorities())
			.extracting("authority")
			.containsExactlyInAnyOrder("ROLE_USER");
	}

	@Test
	@DisplayName("만료된 토큰으로 검증 - 소셜")
	void validateToken_social() {
		// given
		Authentication auth = createMockOAuth2Authentication();
		ReflectionTestUtils.setField(jwtTokenProvider, "accessTokenValidityInSeconds", -3600L);
		String expiredToken = jwtTokenProvider.createAccessToken(auth);

		// when & then
		assertThatThrownBy(() -> jwtTokenProvider.validateToken(expiredToken))
			.isInstanceOf(CustomException.class)
			.satisfies(exception -> {
				CustomException customException = (CustomException)exception;
				assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.EXPIRED_TOKEN);
			});
	}

	@Test
	@DisplayName("만료된 엑세스 토큰으로 검증 - 로컬")
	void validateToken_local() {
		// given
		Authentication auth = createMockUserDetailsAuthentication();
		ReflectionTestUtils.setField(jwtTokenProvider, "accessTokenValidityInSeconds", -3600L);
		String expiredToken = jwtTokenProvider.createAccessToken(auth);

		// when & then
		assertThatThrownBy(() -> jwtTokenProvider.validateToken(expiredToken))
			.isInstanceOf(CustomException.class)
			.satisfies(exception -> {
				CustomException customException = (CustomException)exception;
				assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.EXPIRED_TOKEN);
			});
	}

	@Test
	@DisplayName("잘못된 형식의 토큰 검증")
	void validateToken_malformedToken() {
		// given
		String token = "wrongToken";

		// when & then
		assertThatThrownBy(() -> jwtTokenProvider.validateToken(token))
			.isInstanceOf(CustomException.class)
			.satisfies(exception -> {
				CustomException customException = (CustomException)exception;
				assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.INVALID_TOKEN);
			});
	}

	@Test
	@DisplayName("리프레시 토큰 생성 및 검증")
	void createRefreshToken() {
		// given
		Long userId = 1L;

		// when
		String refreshToken = jwtTokenProvider.createRefreshToken(userId);

		// then
		assertThat(refreshToken).isNotNull();
		assertThat(jwtTokenProvider.validateToken(refreshToken)).isTrue();

		Long extractedUserId = jwtTokenProvider.getUserIdFromRefreshToken(refreshToken);
		assertThat(extractedUserId).isEqualTo(1L);
	}

	@Test
	@DisplayName("블랙리스트에 엑세스 토큰 추가 - 소셜")
	void addToBlackList_social() {
		// given
		Authentication auth = createMockOAuth2Authentication();
		String token = jwtTokenProvider.createAccessToken(auth);
		ValueOperations<String, String> valueOps = mock(ValueOperations.class);

		when(redisTemplate.opsForValue()).thenReturn(valueOps);
		doNothing().when(valueOps).set(anyString(), anyString(), anyLong(), any(TimeUnit.class));

		when(redisTemplate.hasKey("BL:" + token)).thenReturn(true);

		// when
		jwtTokenProvider.addToBlacklist(token);
		boolean isBlacklisted = jwtTokenProvider.isBlacklisted(token);

		// then
		verify(redisTemplate.opsForValue()).set(
			eq("BL:" + token),
			eq("blacklisted"),
			anyLong(),
			eq(TimeUnit.SECONDS)
		);

		assertThat(isBlacklisted).isTrue();
		verify(redisTemplate).hasKey("BL:" + token);
	}

	@Test
	@DisplayName("블랙리스트에 엑세스 토큰 추가 - 로컬")
	void addToBlackList_local() {
		// given
		Authentication auth = createMockUserDetailsAuthentication();
		String token = jwtTokenProvider.createAccessToken(auth);
		ValueOperations<String, String> valueOps = mock(ValueOperations.class);

		when(redisTemplate.opsForValue()).thenReturn(valueOps);
		doNothing().when(valueOps).set(anyString(), anyString(), anyLong(), any(TimeUnit.class));

		when(redisTemplate.hasKey("BL:" + token)).thenReturn(true);

		// when
		jwtTokenProvider.addToBlacklist(token);
		boolean isBlacklisted = jwtTokenProvider.isBlacklisted(token);

		// then
		verify(redisTemplate.opsForValue()).set(
			eq("BL:" + token),
			eq("blacklisted"),
			anyLong(),
			eq(TimeUnit.SECONDS)
		);

		assertThat(isBlacklisted).isTrue();
		verify(redisTemplate).hasKey("BL:" + token);
	}

	@Test
	@DisplayName("만료된 리프레시 토큰 검증")
	void getUserIdFromExpiredRefreshToken() {
		// given
		Long userId = 1L;
		ReflectionTestUtils.setField(jwtTokenProvider, "refreshTokenValidityInSeconds", -604800L);
		String expiredToken = jwtTokenProvider.createRefreshToken(userId);

		// when & then
		assertThatThrownBy(() -> jwtTokenProvider.validateToken(expiredToken))
			.isInstanceOf(CustomException.class)
			.satisfies(exception -> {
				CustomException customException = (CustomException)exception;
				assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.EXPIRED_TOKEN);
			});
	}

	@Test
	@DisplayName("잘못된 시크릿 키로 토큰 검증 - 소셜")
	void validateToken_wrongSecretKey_social() {
		// given
		Authentication auth = createMockOAuth2Authentication();
		String token = jwtTokenProvider.createAccessToken(auth);

		// 다른 시크릿 키로 설정
		ReflectionTestUtils.setField(jwtTokenProvider, "secretKey", "differentSecretKey1234567890ABCDEFGH");
		jwtTokenProvider.init();

		// when & then
		assertThatThrownBy(() -> jwtTokenProvider.validateToken(token))
			.isInstanceOf(CustomException.class)
			.satisfies(exception -> {
				CustomException customException = (CustomException)exception;
				assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.INVALID_TOKEN);
			});
	}

	@Test
	@DisplayName("잘못된 시크릿 키로 토큰 검증 - 로컬")
	void validateToken_wrongSecretKey_local() {
		// given
		Authentication auth = createMockUserDetailsAuthentication();
		String token = jwtTokenProvider.createAccessToken(auth);

		// 다른 시크릿 키로 설정
		ReflectionTestUtils.setField(jwtTokenProvider, "secretKey", "differentSecretKey1234567890ABCDEFGH");
		jwtTokenProvider.init();

		// when & then
		assertThatThrownBy(() -> jwtTokenProvider.validateToken(token))
			.isInstanceOf(CustomException.class)
			.satisfies(exception -> {
				CustomException customException = (CustomException)exception;
				assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.INVALID_TOKEN);
			});
	}
}

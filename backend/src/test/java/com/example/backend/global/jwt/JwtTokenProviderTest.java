package com.example.backend.global.jwt;

import static org.assertj.core.api.Assertions.*;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import com.tripmarket.domain.member.entity.Member;
import com.tripmarket.domain.member.entity.Provider;
import com.tripmarket.domain.member.repository.MemberRepository;
import com.tripmarket.global.auth.AuthenticatedUser;
import com.tripmarket.global.jwt.JwtTokenProvider;
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
		attributes.put("id", "1");
		attributes.put("email", "test@test.com");

		CustomOAuth2User principal = new CustomOAuth2User(
			authorities,
			attributes,
			"id",
			1L,
			"test@test.com"
		);

		return new UsernamePasswordAuthenticationToken(principal, "", authorities);
	}

	private Authentication createMockUserDetailsAuthentication() {
		Collection<GrantedAuthority> authorities =
			Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));

		Member member = Member.builder()
			.email("test@test.com")
			.name("테스트")
			.provider(Provider.LOCAL)
			.build();

		// Reflection을 사용하여 id 설정
		ReflectionTestUtils.setField(member, "id", 1L);

		CustomUserDetails principal = new CustomUserDetails(member);
		return new UsernamePasswordAuthenticationToken(principal, "", authorities);
	}

	@Test
	@DisplayName("정상 토큰 생성 - 소셜")
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
		assertThat(principal.getEmail()).isEqualTo("test@test.com");
	}

	@Test
	@DisplayName("정상 토큰 생성 - 로컬")
	void createAccessToken_local() {
		// given
		Authentication auth = createMockUserDetailsAuthentication();

		// when
		String token = jwtTokenProvider.createAccessToken(auth);

		// then
		assertThat(token).isNotNull();
		assertThat(jwtTokenProvider.validateToken(token)).isTrue();
	}
}

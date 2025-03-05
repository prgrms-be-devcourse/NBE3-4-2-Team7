package com.tripmarket.global.jwt;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.tripmarket.domain.member.entity.Member;
import com.tripmarket.domain.member.entity.Provider;
import com.tripmarket.domain.member.repository.MemberRepository;
import com.tripmarket.global.exception.CustomException;
import com.tripmarket.global.exception.ErrorCode;
import com.tripmarket.global.exception.JwtAuthenticationException;
import com.tripmarket.global.oauth2.CustomOAuth2User;
import com.tripmarket.global.security.CustomUserDetails;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * JWT 토큰 생성, 검증 및 관리를 담당하는 컴포넌트
 * Access Token과 Refresh Token의 생성, 검증, 파싱 기능을 제공
 * 토큰 블랙리스트 관리 및 쿠키 설정도 담당
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

	private final RedisTemplate<String, String> redisTemplate;
	private final MemberRepository memberRepository;

	@Value("${jwt.secret}")
	private String secretKey;

	@Value("${jwt.access-token-expire-time-seconds}")
	private long accessTokenValidityInSeconds;

	@Value("${jwt.refresh-token-expire-time-seconds}")
	private long refreshTokenValidityInSeconds;

	private SecretKey key;

	/**
	 * 시크릿 키를 Base64 디코딩하여 Key 객체 생성
	 * 의존성 주입이 완료된 후 실행됨
	 */
	@PostConstruct
	public void init() {
		this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
	}

	/**
	 * 토큰을 블랙리스트에 추가
	 * Redis에 토큰을 저장하고 남은 유효 시간만큼 유지
	 *
	 * @param token 블랙리스트에 추가할 토큰
	 * @throws JwtAuthenticationException 블랙리스트 추가 실패 시
	 */
	public void addToBlacklist(String token) throws ExpiredJwtException {
		try {
			Claims claims = parseClaims(token);
			long expiration = claims.getExpiration().getTime(); // 토큰 만료 시간
			long now = System.currentTimeMillis(); // 현재 시간
			long remainingTime = (expiration - now) / 1000; // 남은 시간

			if (remainingTime > 0) {
				redisTemplate.opsForValue()
					.set("BL:" + token, "blacklisted", remainingTime, TimeUnit.SECONDS);
				log.info("토큰 블랙리스트 추가 완료, 만료까지 {}초 남음", remainingTime);
			}
		} catch (ExpiredJwtException e) {
			log.warn("만료된 토큰으로 블랙리스트 추가 시도됨: {}", token);
			throw new CustomException(ErrorCode.EXPIRED_TOKEN);

		} catch (Exception e) {
			log.error("토큰 블랙리스트 추가 실패", e);
			throw new CustomException(ErrorCode.TOKEN_REGISTRATION_FAILED);
		}
	}

	/**
	 * 토큰이 블랙리스트에 있는지 확인
	 *
	 * @param token 확인할 토큰
	 * @return 블랙리스트 포함 여부
	 */
	public boolean isBlacklisted(String token) {
		return Boolean.TRUE.equals(redisTemplate.hasKey("BL:" + token));
	}

	/**
	 * 사용자 인증 정보를 기반으로 Access Token을 생성
	 *
	 * @param authentication 인증 정보
	 * @return 생성된 access token
	 */
	public String createAccessToken(Authentication authentication) {
		String authorities = authentication.getAuthorities().stream()
			.map(GrantedAuthority::getAuthority)
			.collect(Collectors.joining(","));

		Date now = new Date();
		Date validity = new Date(now.getTime() + accessTokenValidityInSeconds * 1000);

		// OAuth2 로그인과 일반 로그인 구분
		if (authentication.getPrincipal() instanceof CustomOAuth2User) {
			CustomOAuth2User user = (CustomOAuth2User)authentication.getPrincipal();

			return Jwts.builder()
				.subject(String.valueOf(user.getId()))
				.claim("auth", authorities)
				.claim("email", user.getEmail())
				.issuedAt(now)
				.expiration(validity)
				.signWith(key)
				.compact();

			// 일반 로그인 - CustomUserDetails 사용
		} else if (authentication.getPrincipal() instanceof CustomUserDetails) {
			CustomUserDetails userDetails = (CustomUserDetails)authentication.getPrincipal();
			Member member = userDetails.getMember();

			return Jwts.builder()
				.subject(String.valueOf(member.getId()))
				.claim("auth", authorities)
				.claim("email", member.getEmail())
				.issuedAt(now)
				.expiration(validity)
				.signWith(key)
				.compact();

		} else {
			throw new JwtAuthenticationException("지원하지 않는 인증 방식입니다.");
		}
	}

	/**
	 * Refresh Token 생성
	 *
	 * @Param userId 사용자 ID
	 * @return 생성된 refresh token
	 */
	public String createRefreshToken(Long userId) {
		Date now = new Date();
		Date validity = new Date(now.getTime() + refreshTokenValidityInSeconds * 1000);

		return Jwts.builder()
			.subject(String.valueOf(userId))
			.issuedAt(now)
			.expiration(validity)
			.signWith(key)
			.compact();
	}

	/**
	 * JWT 토큰에서 인증 정보 추출
	 * 만료된 토큰도 처리 가능
	 *
	 * @param token JWT 토큰
	 * @return Spring Security 인증 객체
	 * @throws JwtAuthenticationException 토큰이 유효하지 않을 경우
	 */
	public Authentication getAuthentication(String token) {
		Claims claims = parseClaims(token);
		String email = claims.get("email", String.class);
		Long userId = Long.valueOf(claims.getSubject());
		String provider = claims.get("provider", String.class);

		Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get("auth").toString().split(","))
			.map(SimpleGrantedAuthority::new)
			.toList();

		Member member = memberRepository.findById(userId)
			.orElseThrow(() -> new JwtAuthenticationException("사용자를 찾을 수 없습니다."));

		if (Provider.LOCAL.name().equals(provider)) {
			// 일반 로그인 - CustomUserDetails 사용
			CustomUserDetails userDetails = new CustomUserDetails(member);
			return new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

		} else {
			// OAuth2 로그인
			CustomOAuth2User principal = new CustomOAuth2User(
				authorities,
				Map.of("id", userId, "email", email),
				"id",
				userId,
				email);
			return new UsernamePasswordAuthenticationToken(principal, "", authorities);
		}
	}

	/**
	 * 토큰의 유효성 검증
	 * 서명 검증, 만료 여부 등 확인
	 *
	 * @param token 검증할 토큰
	 * @return 유효성 여부
	 */
	public boolean validateToken(String token) {
		try {
			Jwts.parser()
				.verifyWith(key)
				.build()
				.parseSignedClaims(token);
			return true;
		} catch (SecurityException | MalformedJwtException e) {
			log.error("잘못된 JWT 서명: {}", e.getMessage());
			throw new CustomException(ErrorCode.INVALID_TOKEN);

		} catch (ExpiredJwtException e) {
			log.error("만료된 JWT 토큰: {}", e.getMessage());
			throw new CustomException(ErrorCode.EXPIRED_TOKEN);

		} catch (UnsupportedJwtException e) {
			log.error("지원하지 않는 JWT 토큰: {}", e.getMessage());
			throw new CustomException(ErrorCode.UNSUPPORTED_TOKEN);

		} catch (IllegalArgumentException e) {
			log.error("JWT 토큰이 비어 있음: {}", e.getMessage());
			throw new CustomException(ErrorCode.EMPTY_TOKEN);
		}
	}

	/**
	 * 토큰에서 Claims 추출
	 * 만료된 토큰도 처리 가능
	 *
	 * @param token JWT 토큰
	 * @return Claims 객체
	 */
	private Claims parseClaims(String token) {
		try {
			return Jwts.parser()
				.verifyWith(key)
				.build()
				.parseSignedClaims(token)
				.getPayload();
		} catch (ExpiredJwtException e) {
			// 만료된 토큰이어도 Claims 반환
			return e.getClaims();

		} catch (Exception e) {
			log.error("JWT Claims 파싱 실패: {}", e.getMessage());
			throw new CustomException(ErrorCode.INVALID_TOKEN);
		}
	}

	/**
	 * 만료된 토큰을 포함하여 사용자 ID를 추출
	 * 토큰의 서명이 유효한 경우에만 ID 반환
	 *
	 * @param refreshToken JWT 토큰
	 * @return 사용자 ID
	 * @throws CustomException 토큰이 유효하지 않을 경우
	 */
	public Long getUserIdFromRefreshToken(String refreshToken) {
		try {
			Claims claims = parseClaims(refreshToken);
			return Long.valueOf(claims.getSubject());
		} catch (Exception e) {
			throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
		}
	}

	/**
	 * Authentication 객체에서 권한 정보 추출
	 *
	 * @param authentication 인증 객체
	 * @return 쉼표로 구분된 권한 문자열
	 */
	private String getAuthorities(Authentication authentication) {
		return authentication.getAuthorities().stream()
			.map(GrantedAuthority::getAuthority)
			.collect(Collectors.joining(","));
	}
}

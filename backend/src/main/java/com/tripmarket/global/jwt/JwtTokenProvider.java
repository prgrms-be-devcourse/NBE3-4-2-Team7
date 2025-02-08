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
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.tripmarket.global.exception.JwtAuthenticationException;
import com.tripmarket.global.oauth2.CustomOAuth2User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
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

	@Value("${jwt.secret}")
	private String secretKey;

	@Value("${jwt.access-token-expire-time}")
	private long accessTokenValidityInMilliseconds;

	@Value("${jwt.refresh-token-expire-time-seconds}")
	private long refreshTokenValidityInMilliseconds;

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
	 * HTTP 요청의 쿠키에서 JWT 토큰 추출
	 *
	 * @param request HTTP 요청
	 * @return 쿠키에서 추출한 토큰, 없으면 null
	 */
	public String resolveToken(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("accessToken".equals(cookie.getName())) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}

	/**
	 * Access Token을 담은 쿠키 생성
	 *
	 * @param token JWT 토큰
	 * @return 설정된 쿠키 객체
	 */
	public ResponseCookie createAccessTokenCookie(String token) {
		return ResponseCookie.from("accessToken", token)
			.httpOnly(true)    // JavaScript에서 접근 불가
			.secure(true)      // HTTPS에서만 전송
			.sameSite("Lax")   // CSRF 방지
			.path("/")         // 모든 경로에서 접근 가능
			.maxAge(accessTokenValidityInMilliseconds)
			.build();
	}

	/**
	 * 쿠키 삭제를 위한 빈 쿠키 생성
	 *
	 * @return 만료된 쿠키 객체
	 */
	public ResponseCookie createEmptyCookie() {
		return ResponseCookie.from("accessToken", "")
			.httpOnly(true)
			.secure(true)
			.sameSite("Lax")
			.path("/")
			.maxAge(0)  // 즉시 만료
			.build();
	}

	/**
	 * 토큰을 블랙리스트에 추가
	 * Redis에 토큰을 저장하고 남은 유효 시간만큼 유지
	 *
	 * @param token 블랙리스트에 추가할 토큰
	 * @throws JwtAuthenticationException 블랙리스트 추가 실패 시
	 */
	public void addToBlacklist(String token) {
		try {
			Claims claims = parseClaims(token);
			long expiration = claims.getExpiration().getTime(); // 토큰 만료 시간
			long now = System.currentTimeMillis(); // 현재 시간
			long remainingTime = (expiration - now) / 1000; // 남은 시간

			if (remainingTime > 0) {
				redisTemplate.opsForValue()
					.set("BL:" + token, "blacklisted", remainingTime, TimeUnit.SECONDS);
				log.debug("Token added to blacklist, expires in {} seconds", remainingTime);
			}
		} catch (ExpiredJwtException e) {
			// 만료된 토큰도 블랙리스트에 추가 (보안상 필요할 수 있음)
			Claims claims = e.getClaims();
			redisTemplate.opsForValue()
				.set("BL:" + token, "blacklisted", 300, TimeUnit.SECONDS); // 5분간 유지
			log.debug("Expired token added to blacklist");
		} catch (Exception e) {
			log.error("Failed to add token to blacklist", e);
			throw new JwtAuthenticationException("토큰 블랙리스트 등록 실패");
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
	 * @param authentication 인증 정보
	 * @return 생성된 access token
	 */
	public String createAccessToken(Authentication authentication) {
		Date now = new Date();
		Date validity = new Date(now.getTime() + accessTokenValidityInMilliseconds * 1000);

		CustomOAuth2User user = (CustomOAuth2User)authentication.getPrincipal();

		return Jwts.builder()
			.subject(String.valueOf(user.getId())) // 식별자로 id만 사용
			.claim("auth", getAuthorities(authentication)) // 권한 정보
			.claim("email", user.getEmail()) // email claim 추가
			.issuedAt(now)
			.expiration(validity)
			.signWith(key)
			.compact();
	}

	/**
	 * Refresh Token 생성
	 * 인증 정보 없이 만료 시간만 포함하여 생성
	 * @return 생성된 refresh token
	 */
	public String createRefreshToken() {
		Date now = new Date();
		Date validity = new Date(now.getTime() + refreshTokenValidityInMilliseconds * 1000);

		return Jwts.builder()
			.issuedAt(now)
			.expiration(validity)
			.signWith(key)
			.compact();
	}

	/**
	 * JWT 토큰에서 인증 정보 추출
	 * @param token JWT 토큰
	 * @return Spring Security 인증 객체
	 */
	public Authentication getAuthentication(String token) {
		Claims claims = parseClaims(token);
		Long userId = Long.valueOf(claims.getSubject());
		String email = claims.get("email", String.class);

		Collection<? extends GrantedAuthority> authorities =
			Arrays.stream(claims.get("auth").toString().split(","))
				.map(SimpleGrantedAuthority::new)
				.toList();

		//
		CustomOAuth2User principal = new CustomOAuth2User(
			authorities, // 권한 정보
			Map.of(
				"id", userId,
				"email", email), // OAuth2 속성
			"id", // nameAttributeKey
			userId, // id
			email
		);
		return new UsernamePasswordAuthenticationToken(principal, "", authorities);
	}

	/**
	 * 토큰의 유효성 검증
	 * 서명 검증, 만료 여부 등 확인
	 * @param token 검증할 토큰
	 * @return 유효성 여부
	 * @throws JwtAuthenticationException 토큰이 유효하지 않을 경우
	 */
	public boolean validateToken(String token) {
		try {
			Jwts.parser()
				.verifyWith(key)
				.build()
				.parseSignedClaims(token);
			return true;
		} catch (SecurityException | MalformedJwtException e) {
			log.error("Invalid JWT signature: {}", e.getMessage());
			throw new JwtAuthenticationException("잘못된 JWT 서명입니다.");
		} catch (ExpiredJwtException e) {
			log.error("Expired JWT token: {}", e.getMessage());
			throw new JwtAuthenticationException("만료된 JWT 토큰입니다.");
		} catch (UnsupportedJwtException e) {
			log.error("Unsupported JWT token: {}", e.getMessage());
			throw new JwtAuthenticationException("지원되지 않는 JWT 토큰입니다.");
		} catch (IllegalArgumentException e) {
			log.error("JWT claims string is empty: {}", e.getMessage());
			throw new JwtAuthenticationException("JWT 토큰이 잘못되었습니다.");
		}
	}

	/**
	 * 토큰에서 Claims 추출
	 * @param token JWT 토큰
	 * @return Claims 객체
	 * @throws JwtAuthenticationException 토큰이 만료되었을 경우
	 */
	private Claims parseClaims(String token) {
		try {
			return Jwts.parser()
				.verifyWith(key)
				.build()
				.parseSignedClaims(token)
				.getPayload();
		} catch (ExpiredJwtException e) {
			throw new JwtAuthenticationException("만료된 JWT 토큰입니다.");
		} catch (Exception e) {
			throw new JwtAuthenticationException("유효하지 않은 토큰입니다.");
		}
	}

	/**
	 * 만료된 토큰을 포함하여 사용자 ID를 추출
	 * 토큰의 서명이 유효한 경우에만 ID 반환
	 *
	 * @param token JWT 토큰
	 * @return 사용자 ID
	 * @throws JwtAuthenticationException 토큰이 유효하지 않을 경우
	 */
	public Long getUserIdFromExpiredToken(String token) {
		try {
			return Long.valueOf(parseClaims(token).getSubject());
		} catch (ExpiredJwtException e) {
			// 만료된 토큰이어도 서명이 유효하면 ID 반환
			return Long.valueOf(e.getClaims().getSubject());
		} catch (Exception e) {
			throw new JwtAuthenticationException("유효하지 않은 토큰입니다.");
		}
	}

	/**
	 * Authentication 객체에서 권한 정보 추출
	 * @param authentication 인증 객체
	 * @return 쉼표로 구분된 권한 문자열
	 */
	private String getAuthorities(Authentication authentication) {
		return authentication.getAuthorities().stream()
			.map(GrantedAuthority::getAuthority)
			.collect(Collectors.joining(","));
	}

}

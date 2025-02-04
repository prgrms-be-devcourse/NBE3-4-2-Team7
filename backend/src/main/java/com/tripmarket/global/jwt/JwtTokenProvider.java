package com.tripmarket.global.jwt;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.tripmarket.global.exception.JwtAuthenticationException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtTokenProvider {

	@Value("${jwt.secret}")
	private String secretKey;

	@Value("${jwt.access-token-expire-time}")
	private long accessTokenValidityInMilliseconds;

	@Value("${jwt.refresh-token-expire-time}")
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
	 * 사용자 인증 정보를 기반으로 Access Token을 생성
	 * @param authentication 인증 정보
	 * @return 생성된 access token
	 */
	public String createAccessToken(Authentication authentication) {
		Date now = new Date();
		Date validity = new Date(now.getTime() + accessTokenValidityInMilliseconds * 1000);

		return Jwts.builder()
			.subject(authentication.getName())
			.claim("auth", getAuthorities(authentication))
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

		Collection<? extends GrantedAuthority> authorities =
			Arrays.stream(claims.get("auth").toString().split(","))
				.map(SimpleGrantedAuthority::new)
				.toList();

		UserDetails principal = new User(claims.getSubject(), "", authorities);
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

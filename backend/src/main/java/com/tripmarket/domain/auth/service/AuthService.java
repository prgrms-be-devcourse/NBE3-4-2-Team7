package com.tripmarket.domain.auth.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.tripmarket.global.exception.JwtAuthenticationException;
import com.tripmarket.global.jwt.JwtTokenProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

	private final JwtTokenProvider jwtTokenProvider;
	private final RedisTemplate<String, String> redisTemplate;

	public String refreshAccessToken(String accessToken) {
		// 1. Access Token 블랙리스트 체크
		validateAccessToken(accessToken);

		// 2. 토큰에서 사용자 정보 추출
		Long userId = jwtTokenProvider.getUserIdFromExpiredToken(accessToken);

		// 3. Redis에서 Refresh Token 조회 및 검증
		validateRefreshToken(userId);

		// 3-1. Redis에서 Refresh Token 조회 및 검증

		// 4. 새로운 Access Token 발급
		String newAccessToken = jwtTokenProvider.refreshAccessToken(accessToken);
		log.debug("New access token created for userId: {}", userId);

		// 5. 기존 Access Token을 블랙리스트에 추가
		jwtTokenProvider.addToBlacklist(accessToken);

		log.debug("새 토큰 발급: userId={}", userId);
		return newAccessToken;
	}

	public void logout(String accessToken) {
		try {
			// 1. 토큰에서 사용자 정보 추출 (만료된 토큰도 처리 가능)
			Long userId = jwtTokenProvider.getUserIdFromExpiredToken(accessToken);

			// 2. Access Token 블랙리스트 추가
			jwtTokenProvider.addToBlacklist(accessToken);

			// 3. Refresh Token 삭제
			String refreshTokenKey = "RT:" + userId;
			Boolean deleted = redisTemplate.delete(refreshTokenKey);

			if (Boolean.FALSE.equals(deleted)) {
				log.warn("Refresh Token not found for userId: {}", userId);
			}

			log.debug("로그아웃 처리 완료 - userId: {}", userId);
		} catch (Exception e) {
			log.error("로그아웃 처리 중 오류 발생", e);
			throw new JwtAuthenticationException("로그아웃 처리 중 오류가 발생했습니다.");
		}
	}

	// AccessToken 블랙리스트 체크
	private void validateAccessToken(String accessToken) {
		if (jwtTokenProvider.isBlacklisted(accessToken)) {
			log.warn("블랙리스트된 토큰 사용");
			throw new JwtAuthenticationException("유효하지 않은 토큰입니다.");
		}
	}

	// RefreshToken Redis에서 유효한지 체크
	private void validateRefreshToken(Long userId) {
		String refreshToken = redisTemplate.opsForValue().get("RT:" + userId);
		if (refreshToken == null) {
			log.warn("RefreshToken 없음: userId={}", userId);
			throw new JwtAuthenticationException("Refresh Token이 존재하지 않습니다.");
		}
	}
}

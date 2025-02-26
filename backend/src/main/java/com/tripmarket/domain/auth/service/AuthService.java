package com.tripmarket.domain.auth.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import com.tripmarket.domain.auth.dto.LoginRequestDTO;
import com.tripmarket.domain.auth.dto.SignUpRequestDTO;
import com.tripmarket.domain.member.entity.Member;
import com.tripmarket.domain.member.repository.MemberRepository;
import com.tripmarket.global.exception.JwtAuthenticationException;
import com.tripmarket.global.jwt.JwtTokenProvider;
import com.tripmarket.global.oauth2.CustomOAuth2User;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

	private final JwtTokenProvider jwtTokenProvider;
	private final RedisTemplate<String, String> redisTemplate;
	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;

	public String refreshAccessToken(String refreshToken) {
		// 1. Refresh Token에서 userId 추출
		Long userId = jwtTokenProvider.getUserIdFromRefreshToken(refreshToken);

		// 2. Redis에 저장된 Refresh Token 조회
		String storedRefreshToken = redisTemplate.opsForValue().get("RT:" + userId);
		if (storedRefreshToken == null) {
			throw new JwtAuthenticationException("저장된 Refresh Token이 없습니다.");
		}

		// 3. 전달받은 Refresh Token과 저장된 Token 비교
		if (!refreshToken.equals(storedRefreshToken)) {
			throw new JwtAuthenticationException("Refresh Token이 일치하지 않습니다.");
		}

		// 4. Member 정보 조회
		Member member = memberRepository.findById(userId)
				.orElseThrow(() -> new JwtAuthenticationException("사용자를 찾을 수 없습니다."));

		// 5. 새로운 Access Token 생성
		Authentication authentication = new UsernamePasswordAuthenticationToken(
				member.getEmail(),
				null,
				Collections.singleton(new SimpleGrantedAuthority(member.getRole().name())));

		return jwtTokenProvider.createAccessToken(authentication);
	}

	public void logout(String accessToken) {
		try {
			// 1. 토큰에서 사용자 정보 추출 (만료된 토큰도 처리 가능)
			Long userId = jwtTokenProvider.getUserIdFromRefreshToken(accessToken);

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

	@Transactional
	public void signUp(SignUpRequestDTO signUpRequestDTO) {
		// 중복 검사
		if (memberRepository.findByEmail(signUpRequestDTO.email()).isPresent()) {
			throw new JwtAuthenticationException("이미 가입된 이메일입니다.");
		}

		Member member = new Member(
				signUpRequestDTO.name(),
				signUpRequestDTO.email(),
				passwordEncoder.encode(signUpRequestDTO.password()),
				signUpRequestDTO.imageUrl());

		memberRepository.save(member);
	}

	@Transactional
	public Map<String, String> login(LoginRequestDTO loginRequestDTO) {
		// 1. 회원 존재 여부 확인
		Member member = memberRepository.findByEmail(loginRequestDTO.email())
				.orElseThrow(() -> new JwtAuthenticationException("가입되지 않은 이메일입니다."));

		// 2. 비밀번호 확인
		if (!passwordEncoder.matches(loginRequestDTO.password(), member.getPassword())) {
			throw new JwtAuthenticationException("잘못된 비밀번호입니다.");
		}

		// 3. 일반 인증 객체 생성
		Authentication authentication = new UsernamePasswordAuthenticationToken(
				member.getEmail(),
				null,
				Collections.singleton(new SimpleGrantedAuthority(member.getRole().name())));

		// 4. JWT 토큰 생성
		String accessToken = jwtTokenProvider.createAccessToken(authentication);
		String refreshToken = jwtTokenProvider.createRefreshToken(member.getId());

		// 5. Refresh Token을 Redis에 저장
		redisTemplate.opsForValue()
				.set("RT:" + member.getId(), refreshToken, 7, TimeUnit.DAYS);

		return Map.of(
				"accessToken", accessToken,
				"refreshToken", refreshToken);
	}
}

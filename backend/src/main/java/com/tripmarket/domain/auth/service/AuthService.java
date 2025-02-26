package com.tripmarket.domain.auth.service;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tripmarket.domain.auth.dto.LoginRequestDTO;
import com.tripmarket.domain.auth.dto.SignUpRequestDTO;
import com.tripmarket.domain.member.entity.Member;
import com.tripmarket.domain.member.repository.MemberRepository;
import com.tripmarket.global.exception.JwtAuthenticationException;
import com.tripmarket.global.jwt.JwtTokenProvider;
import com.tripmarket.global.security.CustomUserDetails;
import com.tripmarket.global.util.CookieUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

	private final JwtTokenProvider jwtTokenProvider;
	private final RedisTemplate<String, String> redisTemplate;
	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;
	private final CookieUtil cookieUtil;

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

	public void logout(HttpServletRequest request, HttpServletResponse response) {
		String logoutRefreshToken = cookieUtil.extractRefreshTokenFromCookie(request);
		String logoutAccessToken = cookieUtil.extractAccessTokenFromCookie(request);

		try {
			// 1. Refresh Token에서 사용자 ID 추출
			Long userId = jwtTokenProvider.getUserIdFromRefreshToken(logoutRefreshToken);

			// 2. Access Token 블랙리스트 추가
			jwtTokenProvider.addToBlacklist(logoutAccessToken);

			// 3. Refresh Token 삭제
			String refreshTokenKey = "RT:" + userId;
			Boolean deleted = redisTemplate.delete(refreshTokenKey);
			if (Boolean.FALSE.equals(deleted)) {
				log.warn("리프레시 토큰을 찾을 수 없습니다.: {}", userId);
			}

			// 4. 쿠키 삭제
			ResponseCookie emptyAccessCookie = cookieUtil.createLogoutAccessCookie();
			ResponseCookie emptyRefreshCookie = cookieUtil.createLogoutRefreshCookie();

			response.addHeader(HttpHeaders.SET_COOKIE, emptyAccessCookie.toString());
			response.addHeader(HttpHeaders.SET_COOKIE, emptyRefreshCookie.toString());

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

		// 3. CustomUserDetails를 사용한 인증 객체 생성
		CustomUserDetails userDetails = new CustomUserDetails(member);
		Authentication authentication = new UsernamePasswordAuthenticationToken(
				userDetails, // principal을 CustomUserDetails로 변경
				null, // credentials
				userDetails.getAuthorities() // authorities도 CustomUserDetails에서 가져옴
		);

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

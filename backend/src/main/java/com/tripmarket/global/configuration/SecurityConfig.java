package com.tripmarket.global.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.tripmarket.global.jwt.JwtAuthenticationFilter;
import com.tripmarket.global.jwt.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtTokenProvider jwtTokenProvider;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			// CSRF 설정 비활성화
			.csrf(AbstractHttpConfigurer::disable)

			// 세션 사용하지 않음
			.sessionManagement((sessionManagement) ->
				sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			)

			// 요청에 대한 권한 설정
			.authorizeHttpRequests((authorizeHttpRequests) ->
				authorizeHttpRequests
					.requestMatchers("/auth/**", "/oauth2/**").permitAll()
					.anyRequest().authenticated()
			)

			// JWT 필터 추가
			.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}

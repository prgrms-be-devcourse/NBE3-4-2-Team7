package com.tripmarket.global.configuration;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.tripmarket.global.jwt.JwtAuthenticationFilter;
import com.tripmarket.global.jwt.JwtTokenProvider;
import com.tripmarket.global.oauth2.handler.OAuth2AuthenticationFailureHandler;
import com.tripmarket.global.oauth2.handler.OAuth2AuthenticationSuccessHandler;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtTokenProvider jwtTokenProvider;
	private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
	private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			// CORS 설정 활성화 - corsConfigurationSource 빈을 통해 설정
			.cors(cors -> cors.configurationSource(corsConfigurationSoruce()))

			// CSRF 설정 비활성화 - REST API에서는 불필요
			.csrf(AbstractHttpConfigurer::disable)

			// 세션 설정 - JWT를 사용하므로 세션을 생성하지 않음
			.sessionManagement((sessionManagement) ->
				sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			)

			// 요청에 대한 권한 설정
			.authorizeHttpRequests((authorizeHttpRequests) ->
				authorizeHttpRequests
					.requestMatchers("/auth/**", "/oauth2/**").permitAll()
					.anyRequest().authenticated()
			)

			// OAuth2 로그인 설정
			.oauth2Login(oauth2 -> oauth2
				.successHandler(oAuth2AuthenticationSuccessHandler) // 로그인 성공 시 처리할 핸들러
				.failureHandler(oAuth2AuthenticationFailureHandler) // 로그인 실패 시 처리할 핸들러
			)

			// JWT 필터 추가
			.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSoruce() {
		CorsConfiguration config = new CorsConfiguration();

		// 허용할 프론트엔드 origin 설정
		config.setAllowedOrigins(List.of("http://localhost:3000"));

		// 허용할 HTTP 메서드 설정
		config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

		// 허용할 HTTP 헤더 설정
		config.setAllowedHeaders(List.of("*"));

		// 크리덴셜 허용 설정
		config.setAllowCredentials(true);

		// 프론트엔드에 노출할 헤더 설정
		config.setExposedHeaders(Arrays.asList("Authorization", "Set-Cookie"));

		// 모든 경로에 대해 위의 CORS 설정 적용
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);

		return source;
	}
}

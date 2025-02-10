package com.tripmarket.global.configuration;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();

		// 허용할 프론트엔드 origin 설정
		config.setAllowedOriginPatterns(List.of("http://localhost:3000"));

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

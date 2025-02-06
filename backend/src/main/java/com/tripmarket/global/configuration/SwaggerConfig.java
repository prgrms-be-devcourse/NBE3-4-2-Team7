package com.tripmarket.global.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI customOpenAPI() {
		// Cookie 기반 인증 스키마 정의
		SecurityScheme cookieAuth = new SecurityScheme()
				.type(SecurityScheme.Type.APIKEY)
				.in(SecurityScheme.In.COOKIE)
				.name("accessToken");

		return new OpenAPI()
				.info(new Info()
						.title("Team7 문서")
						.version("1.0.0")
						.description("team7 프로젝트 API 명세서"))
				.components(new Components()
						.addSecuritySchemes("cookieAuth", cookieAuth))
				.addSecurityItem(new SecurityRequirement().addList("cookieAuth"));
	}
}

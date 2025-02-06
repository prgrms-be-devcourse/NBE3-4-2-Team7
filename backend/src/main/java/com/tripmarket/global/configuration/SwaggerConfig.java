package com.tripmarket.global.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI openAPI() {
		return new OpenAPI()
			.info(new Info()
				.title("TripMarket API")
				.description("TripMarket 서비스 API 문서")
				.version("1.0.0"))
			.externalDocs(new ExternalDocumentation()
				.description("GitHub Repository")
				.url("https://github.com/repo"));
	}
}

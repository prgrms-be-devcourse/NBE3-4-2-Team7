package com.tripmarket.global.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.tripmarket.global.jwt.JwtAuthenticationFilter;
import com.tripmarket.global.jwt.JwtTokenProvider;
import com.tripmarket.global.oauth2.handler.OAuth2AuthenticationFailureHandler;
import com.tripmarket.global.oauth2.handler.OAuth2AuthenticationSuccessHandler;
import com.tripmarket.global.oauth2.service.CustomOAuth2UserService;
import com.tripmarket.global.security.service.CustomUserDetailsService;
import com.tripmarket.global.util.CookieUtil;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final CorsConfig corsConfig;
	private final JwtTokenProvider jwtTokenProvider;
	private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
	private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
	private final CustomOAuth2UserService customOAuth2UserService;
	private final CookieUtil cookieUtil;
	private final UserDetailsService userDetailsService;
	private final PasswordEncoder passwordEncoder;

	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter() {
		return new JwtAuthenticationFilter(jwtTokenProvider, cookieUtil);
	}

	@Bean
	public AuthenticationManager authenticationManager() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder);

		return new ProviderManager(authProvider);
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http, CustomUserDetailsService customUserDetailsService)
		throws Exception {
		http
			// CORS 설정 활성화 - corsConfigurationSource 빈을 통해 설정
			.cors(cors -> cors.configurationSource(corsConfig.corsConfigurationSource()))

			.csrf(AbstractHttpConfigurer::disable)

			// H2 콘솔 설정 추가
			.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))

			// 인증 실패 처리 추가
			.exceptionHandling(exception -> exception
				.authenticationEntryPoint((request, response, authException) -> {
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
					response.setContentType("application/json; charset=utf-8");
					response.getWriter().write("{\"message\": \"인증되지 않은 사용자입니다.\"");
				}))

			// 세션 설정 - JWT를 사용하므로 세션을 생성하지 않음
			.sessionManagement(
				(sessionManagement) -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

			// 요청에 대한 권한 설정
			.authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
				// H2 콘솔 관련 경로
				.requestMatchers("/h2-console/**").permitAll()

				// Swagger UI 관련 경로 (swagger-ui.html 추가)
				.requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/api-docs/**").permitAll()
				// 채팅 관련 경로
				.requestMatchers("/chat-test.html", "/chat/**").permitAll()
				.requestMatchers("/", "/auth/**", "/oauth2/**").permitAll()
				.requestMatchers("/", "login/auth/**", "login/oauth2/**").permitAll()

				.requestMatchers(HttpMethod.GET, "/travels/*").permitAll()
				.requestMatchers(HttpMethod.GET, "/travels/**").permitAll()
				.requestMatchers(HttpMethod.GET, "/guides/*").permitAll()
				.requestMatchers(HttpMethod.GET, "/guides/**").permitAll()

				.requestMatchers("/guide-requests/**").permitAll()
				.requestMatchers("/auth/signup", "/auth/login").permitAll()
				.anyRequest().authenticated())

			.userDetailsService(customUserDetailsService)
			.formLogin(formLogin -> formLogin
				.loginProcessingUrl("/auth/login")
				.usernameParameter("email")
				.passwordParameter("password")
				.disable())

			// OAuth2 로그인 설정
			.oauth2Login(oauth2 -> oauth2
				.successHandler(oAuth2AuthenticationSuccessHandler) // 로그인 성공 시 처리할 핸들러
				.failureHandler(oAuth2AuthenticationFailureHandler) // 로그인 실패 시 처리할 핸들러
				.userInfoEndpoint(userinfo -> userinfo
					.userService(customOAuth2UserService)))

			// JWT 필터 추가
			.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}

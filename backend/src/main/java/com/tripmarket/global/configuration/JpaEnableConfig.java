package com.tripmarket.global.configuration;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = {"com.tripmarket.domain"}, excludeFilters = {
	@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {com.tripmarket.domain.chatting.repository.MessageRepository.class})
})
@EntityScan(basePackages = {"com.tripmarket.domain"})
@EnableJpaAuditing
public class JpaEnableConfig {
}

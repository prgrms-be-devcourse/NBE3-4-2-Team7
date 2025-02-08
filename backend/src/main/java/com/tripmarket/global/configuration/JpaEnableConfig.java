package com.tripmarket.global.configuration;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = {"com.tripmarket.domain"})
@EntityScan(basePackages = {"com.tripmarket.domain"})
public class JpaEnableConfig {
}

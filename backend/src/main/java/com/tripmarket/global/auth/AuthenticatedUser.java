package com.tripmarket.global.auth;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

public interface AuthenticatedUser {
	Long getId();

	String getEmail();

	Collection<? extends GrantedAuthority> getAuthorities();
}

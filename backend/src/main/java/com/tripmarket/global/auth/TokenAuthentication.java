package com.tripmarket.global.auth;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TokenAuthentication implements AuthenticatedUser {

	private final Long id;
	private final String email;
	private final Collection<? extends GrantedAuthority> authorities;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public String getEmail() {
		return email;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}
}

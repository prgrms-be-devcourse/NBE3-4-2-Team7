package com.tripmarket.global.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.tripmarket.domain.member.entity.Member;
import com.tripmarket.global.auth.AuthenticatedUser;

public class CustomUserDetails implements UserDetails, AuthenticatedUser {
	private final Member member;

	public CustomUserDetails(Member member) {
		this.member = member;
	}

	@Override
	public Long getId() {
		return member.getId();
	}

	@Override
	public String getEmail() {
		return member.getEmail();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singletonList(new SimpleGrantedAuthority(member.getRole().name()));
	}

	@Override
	public String getPassword() {
		return member.getPassword(); // 암호화된 비밀번호
	}

	@Override
	public String getUsername() {
		return member.getEmail();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	public Member getMember() {
		return member;
	}
}

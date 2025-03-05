package com.tripmarket.global.security.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.tripmarket.domain.member.entity.Member;
import com.tripmarket.domain.member.repository.MemberRepository;
import com.tripmarket.global.security.CustomUserDetails;

@Service
public class CustomUserDetailsService implements UserDetailsService {
	private final MemberRepository memberRepository;

	public CustomUserDetailsService(MemberRepository memberRepository) {
		this.memberRepository = memberRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Member member = memberRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("가입되지 않은 이메일입니다."));

		return new CustomUserDetails(member); // UserDetails 객체 반환
	}
}

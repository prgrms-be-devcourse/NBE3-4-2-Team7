package com.tripmarket.domain.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tripmarket.domain.member.entity.Member;
import com.tripmarket.domain.member.entity.Provider;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
	Optional<Member> findByProviderAndProviderId(Provider provider, String providerId);

	Optional<Member> findByEmail(String email);

	Optional<Member> findByEmailAndProvider(String email, Provider provider);
}

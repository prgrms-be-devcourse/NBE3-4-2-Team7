package com.tripmarket.domain.member.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tripmarket.domain.member.entity.Provider;
import com.tripmarket.domain.member.entity.SocialAccountLink;

@Repository
public interface SocialAccountLinkRepository extends JpaRepository<SocialAccountLink, Long> {
	Optional<SocialAccountLink> findByProviderAndProviderId(Provider provider, String providerId);

	List<SocialAccountLink> findByMemberId(Long memberId);

	boolean existsByMemberIdAndProvider(Long memberId, Provider provider);
}

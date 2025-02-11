package com.tripmarket.domain.member.dto;

import com.tripmarket.domain.member.entity.Member;

public record MemberResponseDTO(
	Long id,
	String email,
	String name,
	String imageUrl,
	Boolean hasGuideProfile
) {
	public static MemberResponseDTO from(Member member) {
		return new MemberResponseDTO(
			member.getId(),
			member.getEmail(),
			member.getName(),
			member.getImageUrl(),
			member.getHasGuideProfile()
		);
	}
}

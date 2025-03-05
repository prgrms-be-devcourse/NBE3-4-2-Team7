package com.tripmarket.domain.member.dto;

import com.tripmarket.domain.member.entity.Member;

public record MemberResponseDto(
	Long id,
	String email,
	String name,
	String imageUrl,
	Boolean hasGuideProfile
) {
	public static MemberResponseDto from(Member member) {
		return new MemberResponseDto(
			member.getId(),
			member.getEmail(),
			member.getName(),
			member.getImageUrl(),
			member.getHasGuideProfile()
		);
	}
}

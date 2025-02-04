package com.tripmarket.domain.member.dto;

import com.tripmarket.domain.member.entity.Member;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberResponseDTO {
	private Long id;
	private String email;
	private String name;
	private String imageUrl;
	private Boolean hasGuideProfile;

	public static MemberResponseDTO from(Member member) {
		return MemberResponseDTO.builder()
			.id(member.getId())
			.email(member.getEmail())
			.name(member.getName())
			.imageUrl(member.getImageUrl())
			.hasGuideProfile(member.getHasGuideProfile())
			.build();
	}
}

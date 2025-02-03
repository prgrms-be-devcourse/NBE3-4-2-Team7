package com.tripmarket.domain.member.entity;

import java.util.List;

import com.tripmarket.domain.guide.entity.Guide;
import com.tripmarket.global.jpa.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Member extends BaseEntity {

	@Column(nullable = false, length = 50)
	private String name; // 회원 이름

	@Column(nullable = false, unique = true, length = 100)
	private String email; // 회원 이메일 (고유값)

	private String password; // 회원 비밀번호

	@Enumerated(EnumType.STRING)
	private Role role; // 회원 역할 (예: 관리자, 사용자)

	@Column(nullable = false)
	private Boolean hasGuideProfile = false; // 가이드 프로필 여부

	@Column(nullable = false)
	private Boolean isDeleted = false; // 회원 탈퇴 여부

	// 연관 관계 설정
	@OneToMany(mappedBy = "member")
	private List<Guide> guides; // 해당 회원이 작성한 가이드 리스트

   /* @OneToMany(mappedBy = "member")
    private List<Review> reviews; 회원이 작성한 리뷰 리스트 */

}

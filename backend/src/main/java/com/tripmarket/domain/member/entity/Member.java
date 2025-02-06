package com.tripmarket.domain.member.entity;

import com.tripmarket.domain.guide.entity.Guide;
import com.tripmarket.global.jpa.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
	private String emailId; // 회원 이메일 (고유값)

	@Column(nullable = false)
	private String password; // 회원 비밀번호

	@Enumerated(EnumType.STRING)
	private Role role; // 회원 역할 (예: 관리자, 사용자)

	@Column(nullable = false)
	private Boolean hasGuideProfile = false; // 가이드 프로필 여부

	@OneToOne
	@JoinColumn(name = "guide_id")
	private Guide guide; // 회원이 가이드인 경우 연결된 Guide 정보

	// // 연관 관계 설정
	// @OneToMany(mappedBy = "member")
	// private List<Guide> guides; // 해당 회원이 작성한 가이드 리스트

	public Member(String name, String email, String password, Role role, boolean hasGuideProfile) {
		this.name = name;
		this.emailId = email;
		this.password = password;
		this.role = role;
		this.hasGuideProfile = hasGuideProfile;
	}

	public boolean isAdmin() {
		return this.role == Role.ADMIN;
	}

	public String getEmail() {
		return this.emailId;
	}

}

package com.tripmarket.domain.member.entity;

import com.tripmarket.domain.guide.entity.Guide;
import com.tripmarket.global.jpa.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "member")
public class Member extends BaseEntity {

	@Column(nullable = false, length = 50)
	private String name; // 회원 이름

	@Column(nullable = false, unique = true, length = 100)
	private String email; // 회원 이메일 (고유값)

	@Column(nullable = false)
	private String password; // 회원 비밀번호

	@Enumerated(EnumType.STRING)
	private Role role; // 회원 역할 (예: 관리자, 사용자)

	@Column(nullable = false)
	private Boolean hasGuideProfile = false; // 가이드 프로필 여부

	/*
	 * 가이드 프로필 생성하려면 무조건 멤버 정보가 있어야 하므로 주인으로 설정
	 *
	 * */
	@OneToOne
	@JoinColumn(name = "guide_id")
	private Guide guide;

   /* @OneToMany(mappedBy = "member")
    private List<Review> reviews; 회원이 작성한 리뷰 리스트 */

	@Builder
	public Member(String email, String password, String name, Role role) {
		this.email = email;
		this.password = password;
		this.name = name;
		this.role = role;
	}

}

package com.tripmarket.domain.member.entity;

import com.tripmarket.domain.guide.entity.Guide;
import com.tripmarket.global.jpa.entity.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "고유 ID")
	private Long id;

	@Column(nullable = false, length = 50)
	private String name; // 회원 이름

	@Column(nullable = false, unique = true, length = 100)
	private String email; // 회원 이메일 (고유값)

	private String password; // 회원 비밀번호, 소셜 로그인은 password가 필요 없으므로 nullable

	@Enumerated(EnumType.STRING)
	private Role role; // 회원 역할 (예: 관리자, 사용자)

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Provider provider; // OAuth2 제공자 (KAKAO, GOOGLE 등)

	@Column(nullable = false)
	private Boolean hasGuideProfile = false; // 가이드 프로필 여부

	@Column(nullable = false, unique = true)
	private String providerId; // OAuth2 회원 고유 ID

	private String imageUrl;

	private String matchingList;

	@OneToOne
	@JoinColumn(name = "guide_id")
	private Guide guide;

	@Builder
	public Member(String name, String email, String providerId, String imageUrl, Provider provider) {
		this.name = name;
		this.email = email;
		this.provider = provider;
		this.providerId = providerId;
		this.imageUrl = imageUrl;
		this.role = Role.ROLE_USER;
	}

	/**
	 * OAuth2 프로필 정보 변경 시 회원 정보 업데이트
	 * 소셜 로그인(카카오, 구글 등) 프로필 정보가 변경되었을 때 호출
	 */
	public void updateOAuth2Profile(String name, String imageUrl) {
		this.name = name;
		this.imageUrl = imageUrl;
	}

	/**
	 * 	멤버에 가이드 프로필 추가하는 함수
	 * */
	public void addGuideProfile(Guide guide) {
		this.guide = guide;
		this.hasGuideProfile = true;
	}

	public boolean hasGuideProfile() {
		return this.hasGuideProfile;
	}

	public Member(String name, String email, String password, Role role, boolean hasGuideProfile) {
		this.name = name;
		this.email = email;
		this.password = password;
		this.role = role;
		this.hasGuideProfile = hasGuideProfile;
	}

	public boolean isAdmin() {
		return this.role == Role.ROLE_ADMIN;
	}

}

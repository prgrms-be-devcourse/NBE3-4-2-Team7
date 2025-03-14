package com.tripmarket.domain.member.entity;

import java.util.HashSet;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.tripmarket.domain.auth.dto.SignupRequestDto;
import com.tripmarket.domain.guide.entity.Guide;
import com.tripmarket.global.jpa.entity.BaseEntity;
import com.tripmarket.global.oauth2.userinfo.OAuth2UserInfo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity(name = "member")
@Table(name = "member", uniqueConstraints = {
	@UniqueConstraint(name = "UK_local_member_email", columnNames = {"email", "provider"})
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "고유 ID")
	private Long id;

	@Column(nullable = false, length = 50)
	private String name; // 회원 이름

	@Column(nullable = false, length = 100)
	private String email; // 회원 이메일 (고유값)

	private String password; // 회원 비밀번호, 소셜 로그인은 password가 필요 없으므로 nullable

	@Enumerated(EnumType.STRING)
	private Role role; // 회원 역할 (예: 관리자, 사용자)

	@Enumerated(EnumType.STRING)
	@Column(columnDefinition = "VARCHAR(10) CHECK (provider IN ('KAKAO', 'GOOGLE', 'GITHUB', 'LOCAL'))")
	private Provider provider; // OAuth2 제공자 (KAKAO, GOOGLE, LOCAL 등)

	@Column(nullable = false)
	private Boolean hasGuideProfile = false; // 가이드 프로필 여부

	@Column(unique = true)
	private String providerId; // OAuth2 회원 고유 ID

	private String imageUrl;

	private String matchingList;

	@OneToOne
	@JoinColumn(name = "guide_id")
	private Guide guide;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public Provider getProvider() {
		return provider;
	}

	public void setProvider(Provider provider) {
		this.provider = provider;
	}

	public Boolean getHasGuideProfile() {
		return hasGuideProfile;
	}

	public void setHasGuideProfile(Boolean hasGuideProfile) {
		this.hasGuideProfile = hasGuideProfile;
	}

	public String getProviderId() {
		return providerId;
	}

	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getMatchingList() {
		return matchingList;
	}

	public void setMatchingList(String matchingList) {
		this.matchingList = matchingList;
	}

	public Guide getGuide() {
		return guide;
	}

	public void setGuide(Guide guide) {
		this.guide = guide;
	}

	@Builder
	private Member(String name, String email, String password, String providerId, String imageUrl, Provider provider) {
		this.name = name;
		this.email = email;
		this.password = password;
		this.provider = provider;
		this.providerId = providerId;
		this.imageUrl = (imageUrl != null && !imageUrl.trim().isEmpty())
			? imageUrl
			: "https://i.imgur.com/yCUGLR3.jpeg"; // 기본 이미지 URL 설정
		this.role = Role.ROLE_USER;
	}

	public static Member createNormalMember(SignupRequestDto signUpRequestDto, PasswordEncoder passwordEncoder) {
		return Member.builder()
			.name(signUpRequestDto.name())
			.email(signUpRequestDto.email())
			.password(passwordEncoder.encode(signUpRequestDto.password()))
			.provider(Provider.LOCAL)
			.providerId(null)
			.imageUrl(signUpRequestDto.imageUrl())
			.build();
	}

	public static Member createSocialMember(OAuth2UserInfo userInfo, Provider provider) {
		return Member.builder()
			.email(userInfo.getEmail())
			.name(userInfo.getName())
			.password(null)
			.provider(provider)
			.providerId(userInfo.getId())
			.imageUrl(userInfo.getImageUrl())
			.build();
	}

	@Column(name = "is_first_login")
	private Boolean isFirstLogin = true; // 최초 로그인 여부

	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<SocialAccountLink> linkedAccounts = new HashSet<>();

	public void addSocialLink(SocialAccountLink socialLink) {
		if (this.linkedAccounts == null) {
			this.linkedAccounts = new HashSet<>();
		}
		this.linkedAccounts.add(socialLink);
		socialLink.setMember(this);
	}

	public void removeSocialLink(Provider provider) {
		if (this.linkedAccounts != null) {
			this.linkedAccounts.removeIf(link -> link.getProvider() == provider);
		}
	}

	public boolean hasSocialLink(Provider provider) {
		if (this.linkedAccounts == null || this.linkedAccounts.isEmpty()) {
			return false;
		}
		return this.linkedAccounts.stream()
			.anyMatch(link -> link.getProvider() == provider);
	}

	// 계정 상태 확인
	public boolean isFirstLogin() {
		return isFirstLogin != null && isFirstLogin;
	}

	public void completeFirstLogin() {
		this.isFirstLogin = false;
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
	 * 멤버에 가이드 프로필 추가하는 함수
	 */
	public void addGuideProfile(Guide guide) {
		this.guide = guide;
		this.hasGuideProfile = true;
	}

	public void deleteGuideProfile() {
		this.guide = null;
		this.hasGuideProfile = false;
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

	/**
	 * 코틀린에서 롬복 인식못해서 Getter 함수 명시적으로 설정
	 * */
	public Guide getGuide() {
		return guide;
	}

}

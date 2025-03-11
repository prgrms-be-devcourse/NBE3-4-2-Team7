package com.tripmarket.domain.member.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "social_account_link",
	uniqueConstraints = {
		@UniqueConstraint(columnNames = {"member_id", "provider", "provider_id"})
	})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SocialAccountLink {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Provider provider;

	@Column(name = "provider_id", nullable = false)
	private String providerId;

	private String email;

	private String name;

	private String profileImageUrl;

	@Column(name = "linked_at", nullable = false)
	private LocalDateTime linkedAt;

	@Column(name = "last_used_at")
	private LocalDateTime lastUsedAt;

	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		this.updatedAt = LocalDateTime.now();
	}

	@Builder
	public SocialAccountLink(Member member, Provider provider, String providerId,
		String email, String name, String profileImageUrl) {
		this.member = member;
		this.provider = provider;
		this.providerId = providerId;
		this.email = email;
		this.name = name;
		this.profileImageUrl = profileImageUrl;
		this.linkedAt = LocalDateTime.now();
	}

	public void updateLastUsed() {
		this.lastUsedAt = LocalDateTime.now();
	}
}

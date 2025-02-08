package com.tripmarket.domain.travel.entity;

import com.tripmarket.global.jpa.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA를 위한 기본 생성자
@Entity
public class TravelCategory extends BaseEntity {

	@Column(nullable = false, length = 50)
	private String name; // 카테고리 이름 (예: "자연", "힐링")

	public TravelCategory(String name) {
		this.name = name;
	}

}

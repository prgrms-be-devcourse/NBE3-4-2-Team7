package com.tripmarket.domain.auth.dto;

public record LoginRequestDTO(
	String email,
	String password
) {
}
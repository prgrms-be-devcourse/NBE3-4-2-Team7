package com.tripmarket.global.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class UnauthorizedException extends RuntimeException {

	private final HttpStatus status;

	public UnauthorizedException(String message) {
		this(message, HttpStatus.UNAUTHORIZED);
	}

	public UnauthorizedException(String message, HttpStatus status) {
		super(message);
		this.status = status;
	}
}

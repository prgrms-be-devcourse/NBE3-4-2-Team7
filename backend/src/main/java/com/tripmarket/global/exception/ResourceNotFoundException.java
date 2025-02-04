package com.tripmarket.global.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException {

	private final HttpStatus status;

	public ResourceNotFoundException(String message) {
		this(message, HttpStatus.NOT_FOUND);
	}

	public ResourceNotFoundException(String message, HttpStatus status) {
		super(message);
		this.status = status;
	}

}

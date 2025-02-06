package com.tripmarket.global.exception;

import org.springframework.http.HttpStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {
	private final HttpStatus status;
	private final String message;
	private final String code;
}

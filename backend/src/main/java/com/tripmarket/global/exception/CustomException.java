package com.tripmarket.global.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

	private final HttpStatus httpStatus;
	private final ErrorCode errorCode;
	private final String errorMessage;

	public CustomException(ErrorCode errorCode) {
		this.errorCode = errorCode;
		this.errorMessage = errorCode.getMessage();
		this.httpStatus = errorCode.getHttpStatus();
	}
}

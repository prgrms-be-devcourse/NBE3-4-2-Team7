package com.tripmarket.global.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

	private final HttpStatus httpStatus;
	private final ErrorCode errorCode;

	public CustomException(ErrorCode errorCode) {
		super(validateErrorCode(errorCode));

		this.errorCode = errorCode;
		this.httpStatus = errorCode.getHttpStatus();
	}

	private static String validateErrorCode(ErrorCode errorCode) {
		if (errorCode == null) {
			throw new IllegalArgumentException("ErrorCode cannot be null");
		}
		if (errorCode.getMessage() == null) {
			throw new IllegalArgumentException("ErrorCode message cannot be null");
		}
		return errorCode.getMessage();
	}
}


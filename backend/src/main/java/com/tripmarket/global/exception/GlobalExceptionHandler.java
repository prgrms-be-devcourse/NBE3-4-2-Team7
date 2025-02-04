package com.tripmarket.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleException(Exception e) {
		return ResponseEntity
			.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body("서버에서 오류가 발생했습니다: " + e.getMessage());
	}

	@ExceptionHandler({CustomException.class})
	public ResponseEntity<CustomErrorResponse> handleCustomException(CustomException ex) {
		CustomErrorResponse errorResponse = new CustomErrorResponse(
			ex.getHttpStatus().name(),
			ex.getErrorCode().name(),
			ex.getMessage());
		return new ResponseEntity<>(errorResponse, ex.getHttpStatus());
	}
}

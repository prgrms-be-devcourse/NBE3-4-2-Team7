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

	@ExceptionHandler(JwtAuthenticationException.class)
	public ResponseEntity<ErrorResponse> handleException(JwtAuthenticationException e) {
		ErrorResponse errorResponse = ErrorResponse.builder()
			.status(e.getStatus())
			.message(e.getMessage())
			.code("JWT_ERROR")
			.build();

		return ResponseEntity
			.status(e.getStatus())
			.body(errorResponse);
	}

	@ExceptionHandler(UnauthorizedException.class)
	public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException e) {
		ErrorResponse errorResponse = ErrorResponse.builder()
			.status(e.getStatus())
			.message(e.getMessage())
			.code("UNAUTHORIZED")
			.build();

		return ResponseEntity
			.status(e.getStatus())
			.body(errorResponse);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException e) {
		ErrorResponse errorResponse = ErrorResponse.builder()
			.status(e.getStatus())
			.message(e.getMessage())
			.code("RESOURCE_NOT_FOUND")
			.build();

		return ResponseEntity
			.status(e.getStatus())
			.body(errorResponse);
	}
}

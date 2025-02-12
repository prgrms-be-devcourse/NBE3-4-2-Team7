package com.tripmarket.global.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

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

	@ExceptionHandler({CustomException.class})
	public ResponseEntity<Map<String, String>> handleCustomException(CustomException ex) {
		Map<String, String> response = new HashMap<>();
		response.put("status", ex.getHttpStatus().name()); // HTTP 상태명 (예: BAD_REQUEST)
		response.put("code", ex.getErrorCode().name());   // 에러 코드 (예: TRAVEL_NOT_FOUND)
		response.put("message", ex.getErrorMessage());    // 사용자에게 보여줄 메시지

		return ResponseEntity.status(ex.getHttpStatus()).body(response);
	}
}

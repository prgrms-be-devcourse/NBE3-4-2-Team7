package com.tripmarket.global.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

	@ExceptionHandler({CustomException.class})
	public ResponseEntity<Map<String, String>> handleCustomException(CustomException ex) {
		Map<String, String> response = new HashMap<>();
		response.put("status", ex.getHttpStatus().name()); // HTTP 상태명 (예: BAD_REQUEST)
		response.put("code", ex.getErrorCode().name());   // 에러 코드 (예: TRAVEL_NOT_FOUND)
		response.put("message", ex.getErrorMessage());    // 사용자에게 보여줄 메시지

		return ResponseEntity.status(ex.getHttpStatus()).body(response);
	}

	// 유효성 검사 실패 처리 핸들러 추가
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValidException(
		MethodArgumentNotValidException ex) {
		Map<String, Object> response = new HashMap<>();
		Map<String, String> errors = new HashMap<>();

		ex.getBindingResult().getAllErrors().forEach(error -> {
			String fieldName = ((FieldError)error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});

		response.put("status", HttpStatus.BAD_REQUEST.name());
		response.put("code", "VALIDATION_ERROR");
		response.put("message", "입력값 검증에 실패했습니다.");
		response.put("errors", errors);

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}

	// 인증 실패 예외 처리 (잘못된 비밀번호 등)
	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException e) {
		ErrorResponse errorResponse = ErrorResponse.builder()
			.status(HttpStatus.UNAUTHORIZED)
			.message("아이디 또는 비밀번호가 일치하지 않습니다.")
			.code("INVALID_CREDENTIALS")
			.build();

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
	}
}

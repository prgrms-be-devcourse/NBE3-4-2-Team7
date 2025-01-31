package com.tripmarket.global.exception;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;

@RestControllerAdvice
public class GlobalExceptionHandler {

	// jakarata.EntityNotFoundException X 재정의 클래스
	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<ErrorMessage> handleEntityNotFoundException(final EntityNotFoundException ex) {
		List<String> errors = new ArrayList<>();
		errors.add(ex.getMessage());

		ErrorMessage errorMessage = new ErrorMessage(errors);
		return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
	}

	// 컨트롤러에서 유효성 검사 실패
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorMessage> handleMethodArgumentNotValidException(
		final MethodArgumentNotValidException ex) {
		List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
		List<String> errors = fieldErrors.stream()
			.map(
				fieldError -> fieldError.getField() + ", " + fieldError.getDefaultMessage()
			)
			.toList();
		ErrorMessage errorMessage = new ErrorMessage(errors);
		return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
	}

	// 서비스에서 유효성 검사 실패
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorMessage> handleConstraintViolatedException(
		ConstraintViolationException ex
	) {
		Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();
		List<String> errors = constraintViolations.stream()
			.map(
				constraintViolation ->
					extractField(constraintViolation.getPropertyPath())
						+ ", "
						+ constraintViolation.getMessage()
			)
			.toList();
		ErrorMessage errorMessage = new ErrorMessage(errors);
		return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleException(Exception e) {
		return ResponseEntity
			.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body("서버에서 오류가 발생했습니다: " + e.getMessage());
	}

	private String extractField(Path path) {
		String[] splittedArray = path.toString().split("[.]");
		int lastIndex = splittedArray.length - 1;
		return splittedArray[lastIndex];
	}
}

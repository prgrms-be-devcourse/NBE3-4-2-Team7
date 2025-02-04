package com.tripmarket.domain.guide.entity;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = LanguagesValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidLanguages {
	String message() default "지원되지 않는 언어입니다.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}

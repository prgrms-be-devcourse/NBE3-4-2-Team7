package com.tripmarket.domain.guide.service;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;

@Service
@Validated
public class GuideValidationService {
	public <T> void checkValid(@Valid T validationType) {

	}
}

package com.tripmarket.domain.guide.entity;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Component
public class LanguagesValidator implements ConstraintValidator<ValidLanguages, String> {
	private static final Set<String> ISO_DISPLAY_LANGUAGE_SET = new HashSet<>();

	@PostConstruct
	public void init() {
		// Locale 데이터 초기화
		for (String language : Locale.getISOLanguages()) {
			Locale locale = new Locale(language);
			ISO_DISPLAY_LANGUAGE_SET.add(locale.getDisplayLanguage());
		}
	}

	@Override
	public boolean isValid(String languages, ConstraintValidatorContext context) {
		if (languages == null) {
			return false;
		}

		String[] languageArray = languages.split(",");
		for (String language : languageArray) {

			language = language.trim();

			if (!ISO_DISPLAY_LANGUAGE_SET.contains(language)) {
				return false;
			}
		}

		return true;
	}
}

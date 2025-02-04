package com.tripmarket.domain.guide.entity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.springframework.stereotype.Component;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Component
public class LanguagesValidator implements ConstraintValidator<ValidLanguages, String> {

	@Override
	public boolean isValid(String languages, ConstraintValidatorContext context) {
		if (languages == null) {
			return false;
		}

		Set<String> ISOLanguagesSet = new HashSet<>(Arrays.asList(Locale.getISOLanguages()));

		String[] languageArray = languages.split(",");
		for (String language : languageArray) {
			language = language.trim();

			if (!ISOLanguagesSet.contains(language)) {
				return false;
			}
		}

		return true;
	}
}

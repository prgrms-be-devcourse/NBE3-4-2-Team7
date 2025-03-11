package com.tripmarket.domain.guide.entity;

// @Component
// public class LanguagesValidator implements ConstraintValidator<ValidLanguages, String> {
// 	private static final Set<String> ISO_DISPLAY_LANGUAGE_SET = new HashSet<>();
//
// 	@PostConstruct
// 	public void init() {
// 		// Locale 데이터 초기화
// 		for (String language : Locale.getISOLanguages()) {
// 			Locale locale = new Locale(language);
// 			ISO_DISPLAY_LANGUAGE_SET.add(locale.getDisplayLanguage());
// 		}
// 	}
//
// 	@Override
// 	public boolean isValid(String languages, ConstraintValidatorContext context) {
// 		if (languages == null) {
// 			return false;
// 		}
//
// 		String[] languageArray = languages.split(",");
// 		for (String language : languageArray) {
//
// 			language = language.trim();
//
// 			if (!ISO_DISPLAY_LANGUAGE_SET.contains(language)) {
// 				return false;
// 			}
// 		}
//
// 		return true;
// 	}
// }

import jakarta.annotation.PostConstruct
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import org.springframework.stereotype.Component
import java.util.*

@Component
class LanguagesValidator : ConstraintValidator<ValidLanguages, String> {

    companion object {
        private val ISO_DISPLAY_LANGUAGE_SET: MutableSet<String> = HashSet()
    }

    @PostConstruct
    fun init() {
        // Locale 데이터 초기화
        Locale.getISOLanguages().forEach { language ->
            val locale = Locale(language)
            ISO_DISPLAY_LANGUAGE_SET.add(locale.displayLanguage)
        }
    }

    override fun isValid(languages: String?, context: ConstraintValidatorContext?): Boolean {
        if (languages.isNullOrBlank()) {
            return false
        }

        return languages.split(",").all { language ->
            ISO_DISPLAY_LANGUAGE_SET.contains(language.trim())
        }
    }
}

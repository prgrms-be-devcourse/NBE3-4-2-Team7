package com.tripmarket.domain.guide.entity;


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
            val locale = Locale.forLanguageTag(language)
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

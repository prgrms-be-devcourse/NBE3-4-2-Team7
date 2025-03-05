package com.tripmarket.domain.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SignUpRequestDto(
	@Email(message = "올바른 이메일 형식이 아닙니다.")
	@NotBlank(message = "이메일은 필수로 입력하셔야합니다.")
	@Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "이메일은 영문 숫자 조합으로만 작성 가능합니다.")
	String email,

	@Pattern(regexp = "^[가-힣a-zA-Z]{2,}$", message = "이름은 한글 또는 영어만 가능하며, 최소 2자 이상이어야 합니다.")
	@NotBlank(message = "이름은 필수입니다.")
	String name,

	@Pattern(
		regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
		message = "비밀번호는 8자 이상, 영문, 숫자, 특수문자를 반드시 포함해야 합니다."
	)
	@NotBlank(message = "비밀번호는 필수입니다.")
	String password,
	String imageUrl,
	Boolean hasGuideProfile
) {
}

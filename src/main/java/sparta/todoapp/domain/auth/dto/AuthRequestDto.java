package sparta.todoapp.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 로그인 및 회원가입에 사용되는 인증 요청 DTO
 */
@Getter
@RequiredArgsConstructor
public class AuthRequestDto {

	@NotBlank // 빈 값이거나 공백이 아님
	@Size(min = 4, max = 10) // 길이 4이상 10이하
	@Pattern(regexp = "[a-z0-9]+") // a ~ z, 0 ~ 9 만 포함, 1개 이상
	private final String username;

	@NotBlank // 빈 값이거나 공백이 아님
	@Size(min = 8, max = 15) // 길이 8이상 15이하
	@Pattern(regexp = "[a-zA-Z0-9]+") // a~z, A~Z, 0~9 만 포함, 1개 이상
	private final String password;
}

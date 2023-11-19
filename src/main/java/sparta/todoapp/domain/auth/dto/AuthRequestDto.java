package sparta.todoapp.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AuthRequestDto {

	@NotBlank
	@Size(min = 4, max = 10)
	@Pattern(regexp = "[a-z0-9]+")
	private final String username;

	@NotBlank
	@Size(min = 8, max = 15)
	@Pattern(regexp = "[a-zA-Z0-9]+")
	private final String password;
}

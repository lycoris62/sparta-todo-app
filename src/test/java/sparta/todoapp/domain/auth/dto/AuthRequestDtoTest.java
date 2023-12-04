package sparta.todoapp.domain.auth.dto;

import static org.assertj.core.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@ActiveProfiles("test")
@DisplayName("로그인 및 회원가입 요청 DTO 테스트")
class AuthRequestDtoTest {

	private Validator validator;

	@BeforeEach
	public void setUp() {
		try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
			validator = factory.getValidator();
		}
	}

	@DisplayName("유효한 username 및 password 요청")
	@Test
	void validate_valid_username() {

		// given & when
		String username = "username";
		String password = "12345678";
		AuthRequestDto authRequestDto = new AuthRequestDto(username, password);

		// then
		assertThat(authRequestDto.getUsername()).isEqualTo(username);
		assertThat(authRequestDto.getPassword()).isEqualTo(password);
	}

	@DisplayName("유효하지 않은 username 및 password 요청")
	@Test
	void validate_invalid_username() {

		// given
		String username = "us";
		String password = "1234";
		AuthRequestDto authRequestDto = new AuthRequestDto(username, password);

		// when
		Set<ConstraintViolation<AuthRequestDto>> violations = validator.validate(authRequestDto);

		// then
		assertThat(violations.size()).isEqualTo(2);
	}
}
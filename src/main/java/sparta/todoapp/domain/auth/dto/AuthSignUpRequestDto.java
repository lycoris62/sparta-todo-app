package sparta.todoapp.domain.auth.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AuthSignUpRequestDto {

    @Pattern(regexp = "^[a-z0-9]{3,10}$") // a ~ z, 0 ~ 9 만 포함, 4이상 10이하
    private final String username;

    @Pattern(regexp = "^[a-zA-Z0-9]{4,15}$") // a~z, A~Z, 0~9 만 포함, 8이상 15이하
    private final String password;

    @Pattern(regexp = "^[a-zA-Z0-9]{4,15}$") // a~z, A~Z, 0~9 만 포함, 8이상 15이하
    private final String confirmPassword;
}

package sparta.todoapp.domain.auth.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


/**
 * 로그인에 사용되는 인증 요청 DTO
 */
@Getter
@RequiredArgsConstructor
public class AuthLoginRequestDto {

    @Pattern(regexp = "^[a-z0-9]{4,10}$") // a ~ z, 0 ~ 9 만 포함, 4이상 10이하
    private final String username;

    @Pattern(regexp = "^[a-zA-Z0-9]{8,15}$") // a~z, A~Z, 0~9 만 포함, 8이상 15이하
    private final String password;
}

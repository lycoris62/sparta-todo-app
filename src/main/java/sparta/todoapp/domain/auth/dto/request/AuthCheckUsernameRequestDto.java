package sparta.todoapp.domain.auth.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 회원가입에 사용되는 유저네임이 기존에 있는지를 확인하는 요청 DTO
 */
@Getter
@RequiredArgsConstructor
public class AuthCheckUsernameRequestDto {

    @Pattern(regexp = "^[a-z0-9]{4,10}$") // a ~ z, 0 ~ 9 만 포함, 4이상 10이하
    private final String username;
}

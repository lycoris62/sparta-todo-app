package sparta.todoapp.domain.auth.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AuthCheckUsernameResponseDto {
    private final boolean hasExistingUsername;
}

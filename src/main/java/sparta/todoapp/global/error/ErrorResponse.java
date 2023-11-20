package sparta.todoapp.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 에러 발생시 응답되는 DTO
 * HttpStatus는 HTTP 응답 정보로 알 수 있으므로, message 만 포함
 */
@Getter
@RequiredArgsConstructor
public class ErrorResponse {

	private final String message;
}
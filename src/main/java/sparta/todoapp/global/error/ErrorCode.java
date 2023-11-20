package sparta.todoapp.global.error;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 에러코드 모음.
 * 특정 에러가 발생 시 해당 Http Status 와 message 가 리턴됨.
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

	INVALID_TOKEN(BAD_REQUEST, "토큰이 유효하지 않습니다."),
	ACCESS_DENIED(BAD_REQUEST, "작성자만 삭제/수정할 수 있습니다."),
	DUPLICATE_USERNAME(BAD_REQUEST, "종복된 username 입니다."),
	USER_NOT_FOUND(BAD_REQUEST, "회원을 찾을 수 없습니다."),
	INVALID_INPUT_LENGTH(BAD_REQUEST, "입력 길이 미충족"),;

	private final HttpStatus httpStatus;
	private final String message;
}

package sparta.todoapp.global.error;

import static sparta.todoapp.global.error.ErrorCode.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import sparta.todoapp.global.error.exception.AccessDeniedException;
import sparta.todoapp.global.error.exception.DuplicateUsernameException;
import sparta.todoapp.global.error.exception.UserNotFoundException;

/**
 * 에러 핸들링 컨트롤러.
 * 모든 예외는 여기서 처리.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * 잘못된 username, password 로 사용자를 찾을 수 없는 경우.
	 */
	@ExceptionHandler(UserNotFoundException.class)
	protected ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException e) {
		ErrorResponse response = new ErrorResponse(USER_NOT_FOUND.getMessage());
		return new ResponseEntity<>(response, USER_NOT_FOUND.getHttpStatus());
	}

	/**
	 * 회원가입 시 중복된 username 이 있는 경우.
	 */
	@ExceptionHandler(DuplicateUsernameException.class)
	protected ResponseEntity<ErrorResponse> handleDuplicateUsernameException(DuplicateUsernameException e) {
		ErrorResponse response = new ErrorResponse(DUPLICATE_USERNAME.getMessage());
		return new ResponseEntity<>(response, DUPLICATE_USERNAME.getHttpStatus());
	}

	/**
	 * 다른 사용자의 할일카드나 댓글에 접근하는 경우.
	 */
	@ExceptionHandler(AccessDeniedException.class)
	protected ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
		ErrorResponse response = new ErrorResponse(ACCESS_DENIED.getMessage());
		return new ResponseEntity<>(response, ACCESS_DENIED.getHttpStatus());
	}

	/**
	 * 할일카드나 댓글에서 잘못된 아이디를 입력한 경우.
	 */
	@ExceptionHandler(IllegalArgumentException.class)
	protected ResponseEntity<ErrorResponse> handleAccessDeniedException(IllegalArgumentException e) {
		ErrorResponse response = new ErrorResponse(e.getMessage());
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
}
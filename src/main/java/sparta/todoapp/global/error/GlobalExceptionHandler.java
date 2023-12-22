package sparta.todoapp.global.error;

import static sparta.todoapp.global.error.ErrorCode.INVALID_INPUT_LENGTH;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import sparta.todoapp.global.error.exception.ServiceException;

/**
 * 에러 핸들링 컨트롤러.
 * 모든 예외는 여기서 처리.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * 모든 예외를 ServiceException 으로 통일, 에러 내의 ErrorCode 로 핸들링
	 */
	@ExceptionHandler(ServiceException.class)
	protected ResponseEntity<ErrorResponse> handleServiceException(ServiceException e) {
		ErrorCode errorCode = e.getErrorCode();
		ErrorResponse response = new ErrorResponse(errorCode.getMessage());
		return new ResponseEntity<>(response, errorCode.getHttpStatus());
	}

	/**
	 * 할일카드나 댓글에서 잘못된 아이디를 입력한 경우.
	 */
	@ExceptionHandler(IllegalArgumentException.class)
	protected ResponseEntity<ErrorResponse> handleAccessDeniedException(IllegalArgumentException e) {
		ErrorResponse response = new ErrorResponse(e.getMessage());
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Validation 라이브러리 종속 예외.
	 * 입력 길이 미충족 시 발생
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
		ErrorResponse response = new ErrorResponse(INVALID_INPUT_LENGTH.getMessage());
		return new ResponseEntity<>(response, INVALID_INPUT_LENGTH.getHttpStatus());
	}
}

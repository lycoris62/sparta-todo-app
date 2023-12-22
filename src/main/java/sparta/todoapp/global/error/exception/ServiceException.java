package sparta.todoapp.global.error.exception;

import lombok.Getter;
import sparta.todoapp.global.error.ErrorCode;

@Getter
public class ServiceException extends RuntimeException {

    private final ErrorCode errorCode;

    public ServiceException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}

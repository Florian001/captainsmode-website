package dev.florian.linz.captainsmode.rest.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ExceptionControllerAdvice extends ResponseEntityExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(ExceptionControllerAdvice.class);

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequestException(BadRequestException e) {
        return logAndConvertException(e, "Bad Request");
    }

    private static ErrorResponse logAndConvertException(CmBuisnessException ex, String message) {
        ErrorCode errorCode = ex.getErrorCode();
        if (errorCode != null) {
            log.error("{} (error code: {})", message, errorCode, ex);
        } else {
            log.error(message, ex);
        }
        return new ErrorResponse(ex.getMessage(), ex.getErrorCode());
    }
}

package dev.florian.linz.captainsmode.rest.error;

import java.io.Serial;

public class CmBuisnessException extends RuntimeException {
    @Serial private static final long serialVersionUID = 1L;
    
    private final ErrorCode errorCode;
    private final String message;
    
    public CmBuisnessException(ErrorCode errorCode, String message) {
        super(errorCode + ": " + message);
        this.errorCode = errorCode;
        this.message = message;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }
}

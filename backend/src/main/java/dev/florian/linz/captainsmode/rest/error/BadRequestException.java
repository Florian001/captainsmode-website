package dev.florian.linz.captainsmode.rest.error;

import java.io.Serial;

public class BadRequestException extends CmBuisnessException {
    @Serial
    private static final long serialVersionUID = 1L;
    
    public BadRequestException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}

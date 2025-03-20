package com.att.tdp.popcorn_palace.exceptions;

import org.springframework.http.HttpStatus;


public class OperationNotAllowedException extends BaseException {

    private static final String DEFAULT_ERROR_CODE = "OPERATION_NOT_ALLOWED";


    public OperationNotAllowedException(String message) {
        super(message, DEFAULT_ERROR_CODE);
    }

    public OperationNotAllowedException(String operation, String reason) {
        super(String.format("Operation '%s' is not allowed: %s", operation, reason), DEFAULT_ERROR_CODE);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.FORBIDDEN;
    }
}

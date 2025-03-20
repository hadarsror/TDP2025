package com.att.tdp.popcorn_palace.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidResourceException extends BaseException {

    private static final String DEFAULT_ERROR_CODE = "INVALID_RESOURCE";


    public InvalidResourceException(String message) {
        super(message, DEFAULT_ERROR_CODE);
    }

    public InvalidResourceException(String fieldName, String reason) {
        super(String.format("Invalid value for field '%s': %s", fieldName, reason), DEFAULT_ERROR_CODE);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
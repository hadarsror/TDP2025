package com.att.tdp.popcorn_palace.exceptions;

import org.springframework.http.HttpStatus;

public abstract class BaseException extends RuntimeException {

    private final String errorCode;

    protected BaseException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }


    protected BaseException(String message, Throwable cause, String errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }


    public String getErrorCode() {
        return errorCode;
    }

    public abstract HttpStatus getHttpStatus();
}
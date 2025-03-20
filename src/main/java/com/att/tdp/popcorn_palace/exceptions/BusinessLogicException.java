package com.att.tdp.popcorn_palace.exceptions;

import org.springframework.http.HttpStatus;


public class BusinessLogicException extends BaseException {

    private static final String DEFAULT_ERROR_CODE = "BUSINESS_LOGIC_VIOLATION";


    public BusinessLogicException(String message) {
        super(message, DEFAULT_ERROR_CODE);
    }

    public BusinessLogicException(String message, String errorCode) {
        super(message, errorCode);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.UNPROCESSABLE_ENTITY;
    }
}
package com.att.tdp.popcorn_palace.exceptions;

import org.springframework.http.HttpStatus;


public class ResourceAlreadyExistsException extends BaseException {

    private static final String DEFAULT_ERROR_CODE = "RESOURCE_ALREADY_EXISTS";

    public ResourceAlreadyExistsException(String message) {
        super(message, DEFAULT_ERROR_CODE);
    }

    public ResourceAlreadyExistsException(String resourceType, String identifier) {
        super(String.format("%s with identifier '%s' already exists", resourceType, identifier), DEFAULT_ERROR_CODE);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.CONFLICT;
    }
}
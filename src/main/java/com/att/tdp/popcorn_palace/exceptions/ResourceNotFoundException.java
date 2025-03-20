package com.att.tdp.popcorn_palace.exceptions;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BaseException {

    private static final String DEFAULT_ERROR_CODE = "RESOURCE_NOT_FOUND";


    public ResourceNotFoundException(String message) {
        super(message, DEFAULT_ERROR_CODE);
    }


    public ResourceNotFoundException(String resourceType, String identifier) {
        super(String.format("%s with identifier '%s' not found", resourceType, identifier), DEFAULT_ERROR_CODE);
    }

    public ResourceNotFoundException(String resourceType, Long identifier) {
        super(String.format("%s with ID %d not found", resourceType, identifier), DEFAULT_ERROR_CODE);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }
}
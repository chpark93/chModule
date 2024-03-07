package com.ch.core.exception;

import com.ch.core.code.Errors;

/**
 * CommonAccessException
 */
public class CommonAccessException extends RuntimeException {

    private final Errors errorConstants;

    public CommonAccessException(String message, Errors errorConstants) {
        super(message);
        this.errorConstants = errorConstants;
    }

    public CommonAccessException(Errors errorConstants) {
        super(errorConstants.getMessage());
        this.errorConstants = errorConstants;
    }

    public Errors getErrorCode() {
        return errorConstants;
    }
}
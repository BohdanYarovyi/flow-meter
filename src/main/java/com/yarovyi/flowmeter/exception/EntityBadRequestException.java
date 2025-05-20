package com.yarovyi.flowmeter.exception;

public class EntityBadRequestException extends RuntimeException {
    public EntityBadRequestException() {
    }

    public EntityBadRequestException(String message) {
        super(message);
    }

    public EntityBadRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public EntityBadRequestException(Throwable cause) {
        super(cause);
    }

    public EntityBadRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

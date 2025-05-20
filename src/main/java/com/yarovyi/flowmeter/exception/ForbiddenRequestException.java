package com.yarovyi.flowmeter.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ForbiddenRequestException extends RuntimeException {
    private final String title;
    private final String details;

    public ForbiddenRequestException(String title, String details) {
        this.title = title;
        this.details = details;
    }

    public ForbiddenRequestException(String message, String title, String details) {
        super(message);
        this.title = title;
        this.details = details;
    }

    public ForbiddenRequestException(String message, Throwable cause, String title, String details) {
        super(message, cause);
        this.title = title;
        this.details = details;
    }

    public ForbiddenRequestException(Throwable cause, String title, String details) {
        super(cause);
        this.title = title;
        this.details = details;
    }

    public ForbiddenRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String title, String details) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.title = title;
        this.details = details;
    }
}

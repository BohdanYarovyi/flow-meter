package com.yarovyi.flowmeter.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AccountAuthenticationException extends RuntimeException {
    private final String detail;

    public AccountAuthenticationException(String detail) {
        this.detail = detail;
    }

    public AccountAuthenticationException(String message, String detail) {
        super(message);
        this.detail = detail;
    }

    public AccountAuthenticationException(String message, Throwable cause, String detail) {
        super(message, cause);
        this.detail = detail;
    }

    public AccountAuthenticationException(Throwable cause, String detail) {
        super(cause);
        this.detail = detail;
    }

    public AccountAuthenticationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String detail) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.detail = detail;
    }
}

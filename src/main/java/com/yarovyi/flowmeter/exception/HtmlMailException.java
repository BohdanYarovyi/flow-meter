package com.yarovyi.flowmeter.exception;

public class HtmlMailException extends Exception {
    public HtmlMailException() {
    }

    public HtmlMailException(String message) {
        super(message);
    }

    public HtmlMailException(String message, Throwable cause) {
        super(message, cause);
    }

    public HtmlMailException(Throwable cause) {
        super(cause);
    }

    public HtmlMailException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

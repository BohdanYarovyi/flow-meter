package com.yarovyi.flowmeter.exception.exceptionHandler.psqlErrorResolver;

import org.postgresql.util.PSQLException;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class PSQLErrorProcessor {
    private final Set<PSQLErrorHandler> handlers;

    public PSQLErrorProcessor(Set<PSQLErrorHandler> handlers) {
        this.handlers = handlers;
    }

    public String processError(PSQLException exception) {
        if (exception == null) {
            throw new IllegalArgumentException("PSQLException parameter is null");
        }

        for (PSQLErrorHandler handler : handlers) {
            if (handler.supportConstraint(exception)) {
                return handler.getErrorMessage(exception);
            }
        }

        return exception.getServerErrorMessage().getDetail();
    }

}

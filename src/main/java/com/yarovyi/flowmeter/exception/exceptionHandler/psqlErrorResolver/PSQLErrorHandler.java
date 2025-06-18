package com.yarovyi.flowmeter.exception.exceptionHandler.psqlErrorResolver;

import org.postgresql.util.PSQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


interface PSQLErrorHandler {
    Logger log = LoggerFactory.getLogger(PSQLErrorHandler.class);

    default boolean supportConstraint(PSQLException exception) {
        if (exception == null) {
            throw new IllegalArgumentException("PSQLException parameter is null");
        }

        if (exception.getServerErrorMessage() == null || exception.getServerErrorMessage().getConstraint() == null) {
            String logMessage = "ServerErrorMessage or Constraint are missing in PSQLException when they are being checked by {}";
            log.warn(logMessage, this.getClass().getSimpleName());

            return false;
        }

        String errorConstraint = exception.getServerErrorMessage().getConstraint();
        return errorConstraint.equals(getSupportedConstraint());
    }

    String getErrorMessage(PSQLException exception);
    String getSupportedConstraint();
}

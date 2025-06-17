package com.yarovyi.flowmeter.exception.exceptionHandler.psqlErrorResolver;

import org.postgresql.util.PSQLException;
import org.springframework.stereotype.Component;

@Component
class PSQLErrorHandlerUniqueEmail implements PSQLErrorHandler {

    public PSQLErrorHandlerUniqueEmail() {}

    @Override
    public String getErrorMessage(PSQLException exception) {
        return "This email is already taken";
    }

    @Override
    public String getSupportedConstraint() {
        return "account_email_key";
    }

}

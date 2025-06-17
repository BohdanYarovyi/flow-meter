package com.yarovyi.flowmeter.exception.exceptionHandler.psqlErrorResolver;

import org.postgresql.util.PSQLException;
import org.springframework.stereotype.Component;

@Component
class PSQLErrorHandlerUniqueLogin implements PSQLErrorHandler {

    public PSQLErrorHandlerUniqueLogin() {}

    @Override
    public String getErrorMessage(PSQLException exception) {
        return "This login is already taken";
    }

    @Override
    public String getSupportedConstraint() {
        return "account_login_key";
    }

}

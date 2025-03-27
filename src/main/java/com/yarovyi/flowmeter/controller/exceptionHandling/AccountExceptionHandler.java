package com.yarovyi.flowmeter.controller.exceptionHandling;

import com.yarovyi.flowmeter.entity.exception.AccountAuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class AccountExceptionHandler {

    @ExceptionHandler(AccountAuthenticationException.class)
    public ErrorResponse handleAccountAuthenticationException(AccountAuthenticationException e) {
        var problemDetail = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        problemDetail.setTitle("Security exception");
        problemDetail.setDetail(e.getDetail());

        return ErrorResponse.builder(e, problemDetail).build();
    }



}

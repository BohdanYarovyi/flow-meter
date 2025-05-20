package com.yarovyi.flowmeter.exception.exceptionHandler;

import org.postgresql.util.PSQLException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class PSQLExceptionHandler {

    // todo: need to refactor this method for creating more human-readable exception
    @ExceptionHandler(PSQLException.class)
    public ErrorResponse handlePsqlException(PSQLException e) {
        String title = "Data error";
        String detail = e.getServerErrorMessage().getDetail();

        var problemDetail = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        problemDetail.setTitle(title);
        problemDetail.setDetail(detail);

        return ErrorResponse
                .builder(e, problemDetail)
                .build();
    }

}

package com.yarovyi.flowmeter.exception.exceptionHandler;

import com.yarovyi.flowmeter.exception.exceptionHandler.psqlErrorResolver.PSQLErrorProcessor;
import lombok.RequiredArgsConstructor;
import org.postgresql.util.PSQLException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@RequiredArgsConstructor
public class PSQLExceptionHandler {
    private final PSQLErrorProcessor psqlErrorProcessor;

    @ExceptionHandler(PSQLException.class)
    public ErrorResponse handlePsqlException(PSQLException e) {
        String title = "Data error";
        String detail = psqlErrorProcessor.processError(e);

        var problemDetail = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        problemDetail.setTitle(title);
        problemDetail.setDetail(detail);

        return ErrorResponse
                .builder(e, problemDetail)
                .build();
    }

}

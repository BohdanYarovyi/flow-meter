package com.yarovyi.flowmeter.exception.exceptionHandler;

import com.yarovyi.flowmeter.exception.EntityBadRequestException;
import com.yarovyi.flowmeter.exception.ForbiddenRequestException;
import com.yarovyi.flowmeter.exception.InvalidParametersException;
import com.yarovyi.flowmeter.exception.SubentityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CommonExceptionHandler {

    @ExceptionHandler(SubentityNotFoundException.class)
    public ErrorResponse handleSubentityNotFoundException(SubentityNotFoundException e) {
        var problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problemDetail.setTitle("Such %s not found".formatted(e.getEntityClass().getSimpleName()));
        problemDetail.setDetail(e.getMessage());

        return ErrorResponse
                .builder(e,problemDetail)
                .build();
    }

    @ExceptionHandler(ForbiddenRequestException.class)
    public ErrorResponse handleForbiddenRequestException(ForbiddenRequestException e) {
        var problemDetail = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        problemDetail.setTitle(e.getTitle());
        problemDetail.setDetail(e.getDetails());

        return ErrorResponse
                .builder(e, problemDetail)
                .build();
    }

    @ExceptionHandler(EntityBadRequestException.class)
    public ErrorResponse handleEntityBadRequestException(EntityBadRequestException e) {
        var problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Bad Request");
        problemDetail.setDetail(e.getMessage());

        return ErrorResponse
                .builder(e, problemDetail)
                .build();
    }

    @ExceptionHandler(InvalidParametersException.class)
    public ErrorResponse handleInvalidParametersException(InvalidParametersException e) {
        var problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Bad Request");
        problemDetail.setDetail(e.getMessage());

        return ErrorResponse
                .builder(e, problemDetail)
                .build();
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    public ErrorResponse handleUnsupportedOperationException(UnsupportedOperationException e) {
        var problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Bad Request");
        problemDetail.setDetail(e.getMessage());

        return ErrorResponse
                .builder(e, problemDetail)
                .build();
    }

}

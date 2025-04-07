package com.yarovyi.flowmeter.controller.exceptionHandling;

import com.yarovyi.flowmeter.entity.exception.EntityValidationException;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.BindingResult;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Optional;

@ControllerAdvice
public class ValidationExceptionHandler {

    @ExceptionHandler(EntityValidationException.class)
    public ErrorResponse handleEntityValidationException(EntityValidationException e) {
        BindingResult bindingResult = e.getBindingResult();

        MessageSourceResolvable firstError = bindingResult.getAllErrors().getFirst();
        String errorMessage = Optional
                .ofNullable(firstError.getDefaultMessage())
                .orElse("Unrecognized validation error");

        var problemDetail = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        problemDetail.setTitle("Validation error");
        problemDetail.setDetail(errorMessage);

        return ErrorResponse
                .builder(e, problemDetail)
                .build();
    }

}

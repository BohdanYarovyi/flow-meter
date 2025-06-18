package com.yarovyi.flowmeter.exception.exceptionHandler;

import com.yarovyi.flowmeter.exception.EmailNotificationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class NotificationExceptionHandler {

    @ExceptionHandler(EmailNotificationException.class)
    public ErrorResponse handleEmailNotificationException(EmailNotificationException e) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        detail.setTitle("Email notification error");
        detail.setDetail(e.getMessage());

        return ErrorResponse.builder(e, detail).build();
    }

}

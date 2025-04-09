package com.yarovyi.flowmeter.entity.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.Getter;

import java.util.Objects;

@Getter
public class SubentityNotFoundException extends EntityNotFoundException {
    private static final String DEFAULT_ERROR_MESSAGE = "%s entity not found";

    private final Class<?> entityClass;

    public SubentityNotFoundException(Class<?> entityClass) {
        this.entityClass = entityClass;
    }

    public SubentityNotFoundException(Exception cause, Class<?> entityClass) {
        super(cause);
        this.entityClass = entityClass;
    }

    public SubentityNotFoundException(String message, Class<?> entityClass) {
        super(message);
        this.entityClass = entityClass;
    }

    public SubentityNotFoundException(String message, Exception cause, Class<?> entityClass) {
        super(message, cause);
        this.entityClass = entityClass;
    }

    @Override
    public String getMessage() {
        String message = super.getMessage();

        if (!Objects.isNull(message) && !message.isEmpty()) {
            return message;
        }
        return DEFAULT_ERROR_MESSAGE.formatted(this.entityClass.getSimpleName());
    }
}

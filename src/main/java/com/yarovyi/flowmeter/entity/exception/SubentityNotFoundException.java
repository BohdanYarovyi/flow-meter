package com.yarovyi.flowmeter.entity.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.Getter;

@Getter
public class SubentityNotFoundException extends EntityNotFoundException {
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

}

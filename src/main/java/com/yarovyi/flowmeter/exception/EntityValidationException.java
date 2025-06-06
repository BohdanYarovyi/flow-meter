package com.yarovyi.flowmeter.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.validation.BindingResult;

@EqualsAndHashCode(callSuper = true)
@Data
public class EntityValidationException extends RuntimeException {
    private final BindingResult bindingResult;

}

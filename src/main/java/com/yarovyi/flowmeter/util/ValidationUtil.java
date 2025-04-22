package com.yarovyi.flowmeter.util;

import com.yarovyi.flowmeter.entity.exception.EntityValidationException;
import org.springframework.validation.BindingResult;

public class ValidationUtil {

    public static void checkOrThrow(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new EntityValidationException(bindingResult);
        }
    }

}

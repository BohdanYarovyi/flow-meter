package com.yarovyi.flowmeter.entity.dto;

import java.time.LocalDate;

public record AccountUpdatedDto(
        Long id,
        String firstname,
        String lastname,
        String patronymic,
        LocalDate dateOfBirth,
        String phone
) {
}

package com.yarovyi.flowmeter.entity.dto;

import java.time.LocalDate;

public record UpdatedPersonalInfoDto(
        String firstname,
        String lastname,
        String patronymic,
        LocalDate dateOfBirth,
        String phone
) {
}

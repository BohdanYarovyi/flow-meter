package com.yarovyi.flowmeter.dto.account;

import java.time.LocalDate;

public record PersonalInfoDto(
        String firstname,
        String lastname,
        String patronymic,
        LocalDate dateOfBirth,
        String phone
) {
}

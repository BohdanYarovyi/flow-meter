package com.yarovyi.flowmeter.entity.domainDto;

import java.time.LocalDate;

public record PersonalInfoDto(
        String firstname,
        String lastname,
        String patronymic,
        LocalDate dateOfBirth,
        String phone
) {
}

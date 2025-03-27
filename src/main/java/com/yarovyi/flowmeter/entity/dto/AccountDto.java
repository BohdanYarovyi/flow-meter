package com.yarovyi.flowmeter.entity.dto;

import com.yarovyi.flowmeter.domain.account.Role;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record AccountDto(
        Long id,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<Role> roles,
        String login,
        String email,
        String firstname,
        String lastname,
        String patronymic,
        LocalDate dateOfBirth,
        String phone
) {
}

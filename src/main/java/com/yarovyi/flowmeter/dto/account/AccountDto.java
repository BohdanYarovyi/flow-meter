package com.yarovyi.flowmeter.dto.account;

import com.yarovyi.flowmeter.entity.account.Role;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public record AccountDto(
        Long id,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Set<Role> roles,
        String login,
        String email,
        String firstname,
        String lastname,
        String patronymic,
        LocalDate dateOfBirth,
        String phone
) {
}

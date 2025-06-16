package com.yarovyi.flowmeter.dto.account;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record AccountCreatedDto(
        @NotBlank(message = "Login must not be blank")
        @Size(min = 3, max = 100, message = "Login length must be between {min} and {max}")
        @Pattern(regexp = "^[^@ ]+$", message = "Login cannot have @ or whitespaces")
        String login,
        @NotBlank(message = "Email must not be blank")
        @Email(message = "Email is not valid")
        @Size(min = 11, max = 100, message = "Email length must be between {min} and {max}")
        String email,
        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 100, message = "Password length must be between {min} and {max}")
        String password,
        String firstname,
        String lastname,
        String patronymic,
        LocalDate dateOfBirth,
        String phone
) {

    public static AccountCreatedDto baseAccount(String login,
                                                String email,
                                                String password,
                                                String firstname,
                                                String lastname) {
        return new AccountCreatedDto(
                login,
                email,
                password,
                firstname,
                lastname,
                null,
                null,
                null
        );
    }
}

package com.yarovyi.flowmeter.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank(message = "Username must be not blank")
        @Size(min = 3, max = 100, message = "Username length must be between {min} and {max}")
        String username,
        @NotBlank(message = "Password must be not blank")
        @Size(min = 3, max = 100, message = "Password length must be between {min} and {max}")
        String password
) {
}

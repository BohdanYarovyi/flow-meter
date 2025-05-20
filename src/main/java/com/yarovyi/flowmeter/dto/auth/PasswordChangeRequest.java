package com.yarovyi.flowmeter.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PasswordChangeRequest(
        @NotBlank(message = "Password must be not blank")
        @Size(min = 3, max = 100, message = "Password length must be between {min} and {max}")
        String currentPassword,
        @NotBlank(message = "Password must be not blank")
        @Size(min = 3, max = 100, message = "Password length must be between {min} and {max}")
        String newPassword
) {
}

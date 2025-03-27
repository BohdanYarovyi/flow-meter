package com.yarovyi.flowmeter.entity.login;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginRequest {
    @NotBlank(message = "Username must be not blank")
    @Size(min = 3, max = 100, message = "Username length must be between {min} and {max}")
    private String username;

    @NotBlank(message = "Password must be not blank")
    @Size(min = 3, max = 100, message = "Password length must be between {min} and {max}")
    private String password;
}

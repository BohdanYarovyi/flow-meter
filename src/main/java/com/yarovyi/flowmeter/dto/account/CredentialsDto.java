package com.yarovyi.flowmeter.dto.account;

import com.yarovyi.flowmeter.mapper.AccountMapper;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Here no transportation passwords, because it is in another place.
 * @see AccountMapper#COMMIT_CREDENTIALS_UPDATES
 *
 * @param login
 * @param email
 */
public record CredentialsDto(
        @NotBlank(message = "Login must not be blank")
        @Size(min = 3, max = 100, message = "Login length must be between {min} and {max}")
        @Pattern(regexp = "^[^@ ]+$", message = "Login cannot have @ or whitespaces")
        String login,

        @NotBlank(message = "Email must not be blank")
        @Email(message = "Email is not valid")
        @Size(min = 11, max = 100, message = "Email length must be between {min} and {max}")
        String email
) {
}

package com.yarovyi.flowmeter.entity.securityDto;

public record PasswordChangeRequest(
        String currentPassword,
        String newPassword
) {
}

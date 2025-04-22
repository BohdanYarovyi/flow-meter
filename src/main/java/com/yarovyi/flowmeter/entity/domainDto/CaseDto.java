package com.yarovyi.flowmeter.entity.domainDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record CaseDto(
        Long id,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        @NotBlank(message = "Case text cannot be blank")
        @Size(max = 255, message = "Text length must be less than {max}")
        String text,
        @Size(min = 0, max = 100, message = "Percent to productivity counting can be from {min} to {max}")
        int percent,
        boolean counting
) {
}

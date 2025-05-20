package com.yarovyi.flowmeter.dto.flow;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record StepDto(
        Long id,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        @NotNull(message = "Day value must be not null")
        LocalDate day,
        List<CaseDto> cases
) {}

package com.yarovyi.flowmeter.entity.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record StepDto(
        Long id,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDate day,
        List<CaseDto> cases
) {}

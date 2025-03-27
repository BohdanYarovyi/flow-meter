package com.yarovyi.flowmeter.entity.dto;

import java.time.LocalDateTime;
import java.util.List;

public record FlowDto(
        Long id,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String title,
        String description,
        int targetPercentage,
        List<StepDto> steps
) {}

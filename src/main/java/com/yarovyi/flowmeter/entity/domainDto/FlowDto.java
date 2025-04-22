package com.yarovyi.flowmeter.entity.domainDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

public record FlowDto(
        Long id,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        @NotBlank(message = "Flow title cannot be blank")
        @Size(max = 100, message = "Flow title length mast be less than {max}")
        String title,
        @Size(max = 1000, message = "Flow description length mast be less than {max}")
        String description,
        @Size(min = 0, max = 100, message = "Target percentage must be between {min} and {max}")
        int targetPercentage,
        List<StepDto> steps
) {}

package com.yarovyi.flowmeter.entity.domainDto;

import java.time.LocalDateTime;

public record CaseDto(
        Long id,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String text,
        int percent,
        boolean counting
) {
}

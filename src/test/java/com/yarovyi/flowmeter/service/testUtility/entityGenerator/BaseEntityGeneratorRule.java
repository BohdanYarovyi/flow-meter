package com.yarovyi.flowmeter.service.testUtility.entityGenerator;

import org.instancio.GeneratorSpecProvider;

import java.time.LocalDateTime;
import java.time.Month;

public interface BaseEntityGeneratorRule {
    LocalDateTime DEFAULT_PAST_TIME = LocalDateTime.of(2025, Month.JUNE,20,0,0);

    GeneratorSpecProvider<Long> ID_GENERATOR = gen -> gen
            .longs()
            .range(1L, 1_000L);

    GeneratorSpecProvider<LocalDateTime> CREATED_AT_GENERATOR = gen -> gen
            .temporal()
            .localDateTime()
            .past()
            .max(DEFAULT_PAST_TIME);

    GeneratorSpecProvider<LocalDateTime> UPDATED_AT_GENERATOR = gen -> gen
            .temporal()
            .localDateTime()
            .past()
            .min(DEFAULT_PAST_TIME)
            .nullable();

    GeneratorSpecProvider<Boolean> DELETED_GENERATOR = gen -> gen
            .booleans()
            .probability(0.1);

}

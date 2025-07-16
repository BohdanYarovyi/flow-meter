package com.yarovyi.flowmeter.testUtility.entityGenerator;

import org.instancio.GeneratorSpecProvider;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;

public interface StepGeneratorRule {

    GeneratorSpecProvider<LocalDate> DAY_GENERATOR = gen -> gen
            .temporal()
            .localDate()
            .past()
            .min(LocalDate.of(1950, 1, 1));

}

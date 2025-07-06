package com.yarovyi.flowmeter.service.testUtility.entityGenerator;

import org.instancio.GeneratorSpecProvider;

public interface CaseGeneratorRule {
    GeneratorSpecProvider<String> TEXT_GENERATOR = gen -> gen
            .text()
            .loremIpsum()
            .words(10);

    GeneratorSpecProvider<Integer> PERCENT_GENERATOR = gen -> gen
            .ints()
            .range(0, 100)
            .nullable();

    GeneratorSpecProvider<Boolean> COUNTING_GENERATOR = gen -> gen
            .booleans()
            .probability(0.1);

}

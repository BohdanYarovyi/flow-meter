package com.yarovyi.flowmeter.service.testUtility.entityGenerator;

import org.instancio.GeneratorSpecProvider;

public interface RoleGeneratorRule {

    GeneratorSpecProvider<Long> ID_GENERATOR = gen -> gen
            .longs()
            .range(1L, 1_000L);

    GeneratorSpecProvider<String> ROLE_NAME_GENERATOR = gen -> gen
            .string()
            .length(10)
            .upperCase();

}

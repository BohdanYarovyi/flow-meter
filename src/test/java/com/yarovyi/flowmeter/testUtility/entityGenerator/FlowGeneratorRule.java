package com.yarovyi.flowmeter.testUtility.entityGenerator;

import org.instancio.GeneratorSpecProvider;
import org.instancio.Instancio;

public interface FlowGeneratorRule {

    GeneratorSpecProvider<String> TITLE_GENERATOR = gen -> gen
            .text()
            .loremIpsum()
            .words(5);

    GeneratorSpecProvider<String> DESCRIPTION_GENERATOR = gen -> gen
            .oneOf(
                    null,
                    Instancio.gen().text().loremIpsum().words(25).get()
            );

    GeneratorSpecProvider<Integer> TARGET_PERCENTAGE_GENERATOR = gen -> gen
            .ints()
            .range(0, 100);

}

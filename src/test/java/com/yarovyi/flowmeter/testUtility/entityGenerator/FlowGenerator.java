package com.yarovyi.flowmeter.testUtility.entityGenerator;

import com.yarovyi.flowmeter.entity.flow.Flow;
import org.instancio.Instancio;
import org.instancio.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.yarovyi.flowmeter.testUtility.entityGenerator.BaseEntityGenerator.generateWithBaseEntity;
import static org.instancio.Select.field;

public class FlowGenerator implements FlowGeneratorRule {

    private static Model<Flow> base() {
        return generateWithBaseEntity(Flow.class)
                .generate(field(Flow::getTitle), TITLE_GENERATOR)
                .generate(field(Flow::getDescription), DESCRIPTION_GENERATOR)
                .generate(field(Flow::getTargetPercentage), TARGET_PERCENTAGE_GENERATOR)
                .toModel();
    }

    public static Flow oneFlow() {
        int stepsCount = 5;
        int casesCount = 3;

        return oneFlow(stepsCount, casesCount);
    }

    public static Flow oneFlow(int stepsCount, int casesCount) {
        return Instancio.of(base())
                .supply(field(Flow::getSteps), () -> StepGenerator.steps(stepsCount, casesCount))
                .create();
    }

    public static List<Flow> flows() {
        int count = 5;
        int stepsCount = 5;
        int casesCount = 3;

        Set<Flow> flowsSet = flows(count, stepsCount, casesCount);
        return new ArrayList<>(flowsSet);
    }

    public static Set<Flow> flows(int count, int stepsCount, int casesCount) {
        Model<Flow> base = Instancio.of(base())
                .supply(field(Flow::getSteps), () -> StepGenerator.steps(stepsCount, casesCount))
                .toModel();

        return Instancio.ofSet(base)
                .size(count)
                .create();
    }

}

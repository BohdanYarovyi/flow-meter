package com.yarovyi.flowmeter.testUtility.entityGenerator;

import com.yarovyi.flowmeter.entity.flow.Step;
import org.instancio.Instancio;
import org.instancio.Model;

import java.util.Set;

import static com.yarovyi.flowmeter.testUtility.entityGenerator.BaseEntityGenerator.generateWithBaseEntity;
import static org.instancio.Select.field;

public class StepGenerator implements StepGeneratorRule {

    private static Model<Step> base() {
        return generateWithBaseEntity(Step.class)
                .generate(field(Step::getDay), DAY_GENERATOR)
                .toModel();
    }

    public static Step oneStep() {
        return oneStep(10);
    }


    public static Step oneStep(int caseCount) {
        return Instancio.of(base())
                .supply(field(Step::getCases), () -> CaseGenerator.cases(caseCount))
                .create();
    }

    public static Set<Step> steps(int count, int caseCount) {
        Model<Step> base = Instancio.of(base())
                .supply(field(Step::getCases), () -> CaseGenerator.cases(caseCount))
                .toModel();

        return Instancio.ofSet(base)
                .size(count)
                .create();
    }

}

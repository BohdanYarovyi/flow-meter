package com.yarovyi.flowmeter.service.testUtility.entityGenerator;

import com.yarovyi.flowmeter.entity.flow.Case;
import org.instancio.Instancio;
import org.instancio.Model;

import java.util.List;
import java.util.Set;

import static com.yarovyi.flowmeter.service.testUtility.entityGenerator.BaseEntityGenerator.generateWithBaseEntity;
import static org.instancio.Select.field;

public class CaseGenerator implements CaseGeneratorRule {

    private static Model<Case> base() {
        return generateWithBaseEntity(Case.class)
                .generate(field(Case::getText), TEXT_GENERATOR)
                .generate(field(Case::getPercent), PERCENT_GENERATOR)
                .generate(field(Case::isCounting), COUNTING_GENERATOR)
                .toModel();
    }

    public static Case oneCase() {
        return Instancio.of(base()).create();
    }

    public static Set<Case> cases(int count) {
        return Instancio.ofSet(base())
                .size(count)
                .create();
    }

}

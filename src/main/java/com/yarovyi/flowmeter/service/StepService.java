package com.yarovyi.flowmeter.service;

import com.yarovyi.flowmeter.domain.flow.Flow;
import com.yarovyi.flowmeter.domain.flow.Step;

import java.util.Optional;

public interface StepService {
    Optional<Step> getStepById(Long stepId);
    Step createStepForFlow(Step step, Flow flow);
}

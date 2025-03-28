package com.yarovyi.flowmeter.service;

import com.yarovyi.flowmeter.domain.flow.Flow;
import com.yarovyi.flowmeter.domain.flow.Step;

public interface StepService {
    Long createStepForFlow(Step step, Flow flow);
}

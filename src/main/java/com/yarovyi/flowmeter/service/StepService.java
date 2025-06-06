package com.yarovyi.flowmeter.service;

import com.yarovyi.flowmeter.entity.flow.Flow;
import com.yarovyi.flowmeter.entity.flow.Step;

import java.util.Optional;

public interface StepService {
    Optional<Step> getStepById(Long stepId);
    Step createStepForFlow(Step step, Flow flow);
    void deleteStepById(Long stepId);
    Long getTargetPercentageByStepId(Long stepId);

    boolean checkOwnership(Long stepId, Long accountId);
    void checkOwnershipOrElseThrow(Long stepId, Long accountId);
}

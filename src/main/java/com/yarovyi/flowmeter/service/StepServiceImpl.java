package com.yarovyi.flowmeter.service;

import com.yarovyi.flowmeter.domain.flow.Flow;
import com.yarovyi.flowmeter.domain.flow.Step;
import com.yarovyi.flowmeter.entity.exception.ForbiddenRequestException;
import com.yarovyi.flowmeter.repository.StepRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class StepServiceImpl implements StepService {
    private final StepRepository stepRepository;


    @Override
    public Optional<Step> getStepById(Long stepId) {
        return this.stepRepository.findById(stepId);
    }


    @Override
    public Step createStepForFlow(Step step, Flow flow) {
        if (Objects.isNull(step) || Objects.isNull(flow))
            throw new NullPointerException("step or flow is null");

        if (this.stepRepository.existsStepByDayAndFlow_Id(step.getDay(), flow.getId()))
            throw new ForbiddenRequestException(
                    "Forbidden step creation",
                    "Step with date: %s already exists in flow %s".formatted(step.getDay(), flow.getTitle())
            );

        flow.getSteps().add(step);
        step.setFlow(flow);

        return this.stepRepository.save(step);

    }


}

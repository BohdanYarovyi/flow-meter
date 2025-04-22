package com.yarovyi.flowmeter.service;

import com.yarovyi.flowmeter.domain.flow.Flow;
import com.yarovyi.flowmeter.domain.flow.Step;
import com.yarovyi.flowmeter.entity.exception.ForbiddenRequestException;
import com.yarovyi.flowmeter.entity.exception.SubentityNotFoundException;
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

        if (this.stepRepository.existsStepByDayAndFlow_Id(step.getDay(), flow.getId())) {
            var title = "Forbidden step creation";
            var message = String.format("Step with date: %s already exists in flow '%s'", step.getDay(), flow.getTitle());

            throw new ForbiddenRequestException(title, message);
        }

        flow.getSteps().add(step);
        step.setFlow(flow);

        return this.stepRepository.save(step);
    }


    @Override
    public void deleteStepById(Long stepId) {
        if (Objects.isNull(stepId))
            throw new NullPointerException("stepId is null");

        Step step = this.stepRepository
                .findById(stepId)
                .orElseThrow(() -> new SubentityNotFoundException(Step.class));

        // soft-delete
        step.delete();
        this.stepRepository.save(step);
    }


    @Override
    public boolean checkOwnership(Long stepId, Long accountId) {
        return this.stepRepository.existsByIdAndFlow_Account_Id(stepId, accountId);
    }


    @Override
    public void checkOwnershipOrElseThrow(Long stepId, Long accountId) {
        if (!checkOwnership(stepId, accountId)){
            throw new SubentityNotFoundException("Account has not such step", Step.class);
        }
    }


}

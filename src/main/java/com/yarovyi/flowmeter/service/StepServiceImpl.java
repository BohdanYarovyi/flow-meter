package com.yarovyi.flowmeter.service;

import com.yarovyi.flowmeter.entity.flow.Flow;
import com.yarovyi.flowmeter.entity.flow.Step;
import com.yarovyi.flowmeter.exception.ForbiddenRequestException;
import com.yarovyi.flowmeter.exception.SubentityNotFoundException;
import com.yarovyi.flowmeter.repository.StepRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class StepServiceImpl implements StepService {
    private final StepRepository stepRepository;


    @Transactional(readOnly = true)
    @Override
    public Optional<Step> getStepById(Long stepId) {
        return this.stepRepository.getStepWithEagerFetchById(stepId);
    }


    @Transactional
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


    @Transactional
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


    @Transactional(readOnly = true)
    @Override
    public Long getTargetPercentageByStepId(Long stepId) {
        Step step = this.getStepById(stepId)
                        .orElseThrow(() -> new SubentityNotFoundException(Step.class));

        return (long) step.getFlow().getTargetPercentage();
    }


    @Transactional(readOnly = true)
    @Override
    public boolean checkOwnership(Long stepId, Long accountId) {
        return this.stepRepository.existsByIdAndFlow_Account_Id(stepId, accountId);
    }

    @Transactional(readOnly = true)
    @Override
    public void checkOwnershipOrElseThrow(Long stepId, Long accountId) {
        if (!checkOwnership(stepId, accountId)){
            throw new SubentityNotFoundException("Account has not such step", Step.class);
        }
    }


}

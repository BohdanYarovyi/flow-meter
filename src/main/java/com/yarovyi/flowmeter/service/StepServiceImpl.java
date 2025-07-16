package com.yarovyi.flowmeter.service;

import com.yarovyi.flowmeter.entity.flow.Flow;
import com.yarovyi.flowmeter.entity.flow.Step;
import com.yarovyi.flowmeter.exception.EntityBadRequestException;
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
        if (stepId == null) {
            throw new IllegalArgumentException("Parameter 'steId' is null");
        }

        return stepRepository.getStepWithEagerFetchById(stepId);
    }


    @Transactional
    @Override
    public Step createStepForFlow(Step step, Flow flow) {
        if (step == null || flow == null) {
            throw new IllegalArgumentException("Parameters 'step' or 'flow' are null");
        }

        if (step.getId() != null) {
            throw new IllegalArgumentException("Step.id must be null");
        }

        if (stepRepository.existsStepByDayAndFlow_Id(step.getDay(), flow.getId())) {
            var title = "Forbidden step creation";
            var message = String.format("Step with date: %s already exists in flow '%s'", step.getDay(), flow.getTitle());

            throw new ForbiddenRequestException(title, message);
        }

        flow.getSteps().add(step);
        step.setFlow(flow);

        return stepRepository.save(step);
    }


    @Transactional
    @Override
    public void deleteStepById(Long stepId) {
        if (stepId == null) {
            throw new IllegalArgumentException("Parameter 'stepId' is null");
        }

        Step step = stepRepository
                .findById(stepId)
                .orElseThrow(() -> new SubentityNotFoundException(Step.class));

        // soft-delete
        step.delete();
        stepRepository.save(step);
    }


    @Transactional(readOnly = true)
    @Override
    public Long getTargetPercentageByStepId(Long stepId) {
        if (stepId == null) {
            throw new IllegalArgumentException("Parameter 'stepId' is null");
        }

        Step step = getStepById(stepId)
                .orElseThrow(() -> new SubentityNotFoundException(Step.class));

        return (long) step.getFlow().getTargetPercentage();
    }


    @Transactional(readOnly = true)
    @Override
    public boolean checkOwnership(Long stepId, Long accountId) {
        if (stepId == null || accountId == null) {
            throw new IllegalArgumentException("Parameters 'stepId' or 'accountId' are null");
        }

        return stepRepository.existsByIdAndFlow_Account_Id(stepId, accountId);
    }

    @Transactional(readOnly = true)
    @Override
    public void checkOwnershipOrElseThrow(Long stepId, Long accountId) {
        if (!checkOwnership(stepId, accountId)) {
            throw new SubentityNotFoundException("Account has not such step", Step.class);
        }
    }

}

package com.yarovyi.flowmeter.service;

import com.yarovyi.flowmeter.domain.flow.Case;
import com.yarovyi.flowmeter.domain.flow.Step;
import com.yarovyi.flowmeter.repository.CaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@RequiredArgsConstructor
@Service
public class CaseServiceImpl implements CaseService {
    private final CaseRepository caseRepository;


    @Override
    public Case createCaseForStepById(Step step,Case case1) {
        if (Objects.isNull(case1) || Objects.isNull(step))
            throw new NullPointerException("case or step are null");

        step.getCases().add(case1);
        case1.setStep(step);

        return this.caseRepository.save(case1);
    }


}

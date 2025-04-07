package com.yarovyi.flowmeter.service;

import com.yarovyi.flowmeter.domain.flow.Case;
import com.yarovyi.flowmeter.domain.flow.Step;
import com.yarovyi.flowmeter.entity.exception.EntityBadRequestException;
import com.yarovyi.flowmeter.entity.exception.SubentityNotFoundException;
import com.yarovyi.flowmeter.repository.CaseRepository;
import com.yarovyi.flowmeter.util.CaseMapper;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.yarovyi.flowmeter.util.CaseMapper.*;

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


    // todo: mb here must be void, cause it is more check method than access method
    @Override
    public Case getCaseByIdAndAccountId(Long caseId, Long accountId) {
        return this.caseRepository
                .findCaseByIdAndAccountId(caseId, accountId)
                .orElseThrow(() -> new SubentityNotFoundException(Case.class));
    }


    @Override
    public Case edit(Case editedCase) {
        if (Objects.isNull(editedCase))
            throw new NullPointerException("editedCase is null");

        if (Objects.isNull(editedCase.getId()))
            throw new EntityBadRequestException("To update case caseID is required, but it is null");

        Case existCase = this.caseRepository
                .findById(editedCase.getId())
                .orElseThrow(() -> new SubentityNotFoundException(Case.class));
        existCase = COMMIT_CASE_UPDATE.apply(existCase, editedCase);

        return this.caseRepository.save(existCase);
    }


}

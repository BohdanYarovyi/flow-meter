package com.yarovyi.flowmeter.service;

import com.yarovyi.flowmeter.entity.flow.Case;
import com.yarovyi.flowmeter.entity.flow.Step;
import com.yarovyi.flowmeter.exception.EntityBadRequestException;
import com.yarovyi.flowmeter.exception.SubentityNotFoundException;
import com.yarovyi.flowmeter.repository.CaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static com.yarovyi.flowmeter.mapper.CaseMapper.*;

@RequiredArgsConstructor
@Service
public class CaseServiceImpl implements CaseService {
    private final CaseRepository caseRepository;

    @Transactional
    @Override
    public Case createCaseForStep(Step step, Case case1) {
        if (Objects.isNull(case1) || Objects.isNull(step))
            throw new NullPointerException("case or step are null");

        step.getCases().add(case1);
        case1.setStep(step);

        return this.caseRepository.save(case1);
    }


    @Transactional
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


    @Transactional
    @Override
    public void deleteCaseById(Long caseId) {
        if (Objects.isNull(caseId))
            throw new NullPointerException("CaseId is null");

        Case case1 = this.caseRepository
                .findById(caseId)
                .orElseThrow(() -> new SubentityNotFoundException(Case.class));

        // soft-delete
        case1.delete();
        this.caseRepository.save(case1);
    }


    @Transactional(readOnly = true)
    @Override
    public boolean checkOwnership(Long caseId, Long accountId) {
        return this.caseRepository.existsByIdAndAccountId(caseId, accountId);
    }


    @Transactional(readOnly = true)
    @Override
    public void checkOwnershipOrElseThrow(Long caseId, Long accountId) {
        if (!checkOwnership(caseId, accountId)) {
            throw new SubentityNotFoundException("Account has not such case", Case.class);
        }
    }


}

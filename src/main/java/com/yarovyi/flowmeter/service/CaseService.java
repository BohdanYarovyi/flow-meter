package com.yarovyi.flowmeter.service;

import com.yarovyi.flowmeter.entity.flow.Case;
import com.yarovyi.flowmeter.entity.flow.Step;

public interface CaseService {
    Case createCaseForStep(Step step, Case case1);
    Case edit(Case editedCase);
    void deleteCaseById(Long caseId);

    boolean checkOwnership(Long caseId, Long accountId);
    void checkOwnershipOrElseThrow(Long caseId, Long accountId);
}

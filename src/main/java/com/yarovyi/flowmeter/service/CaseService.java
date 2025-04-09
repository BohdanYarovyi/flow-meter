package com.yarovyi.flowmeter.service;

import com.yarovyi.flowmeter.domain.flow.Case;
import com.yarovyi.flowmeter.domain.flow.Step;

public interface CaseService {
    Case createCaseForStepById(Step step, Case case1);
    Case edit(Case editedCase);

    boolean checkOwnership(Long caseId, Long accountId);
    void checkOwnershipOrElseThrow(Long caseId, Long accountId);
}

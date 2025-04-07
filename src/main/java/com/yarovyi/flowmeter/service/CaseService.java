package com.yarovyi.flowmeter.service;

import com.yarovyi.flowmeter.domain.flow.Case;
import com.yarovyi.flowmeter.domain.flow.Step;

public interface CaseService {
    Case createCaseForStepById(Step step, Case case1);
    Case getCaseByIdAndAccountId(Long caseId, Long accountId);
    Case edit(Case editedCase);
}

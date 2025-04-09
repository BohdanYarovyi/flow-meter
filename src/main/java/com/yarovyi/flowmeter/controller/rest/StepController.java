package com.yarovyi.flowmeter.controller.rest;

import com.yarovyi.flowmeter.domain.account.Account;
import com.yarovyi.flowmeter.domain.flow.Case;
import com.yarovyi.flowmeter.domain.flow.Step;
import com.yarovyi.flowmeter.entity.dto.CaseDto;
import com.yarovyi.flowmeter.entity.exception.SubentityNotFoundException;
import com.yarovyi.flowmeter.service.AccountService;
import com.yarovyi.flowmeter.service.CaseService;
import com.yarovyi.flowmeter.service.StepService;
import com.yarovyi.flowmeter.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.Map;

import static com.yarovyi.flowmeter.util.CaseMapper.CASE_TO_DTO;
import static com.yarovyi.flowmeter.util.CaseMapper.DTO_TO_CASE;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/steps")
public class StepController {
    private final AccountService accountService;
    private final StepService stepService;
    private final CaseService caseService;


    @PostMapping("/{stepId:\\d+}/cases")
    public ResponseEntity<CaseDto> createCaseForStep(@PathVariable(name = "stepId") Long stepId,
                                                  @RequestBody CaseDto caseDto,
                                                  Principal principal) {
        Account account = SecurityUtil.getCurrentAccount(this.accountService, principal);
        this.stepService.checkOwnership(stepId, account.getId());

        Step step = this.stepService
                .getStepById(stepId)
                .orElseThrow(() -> new SubentityNotFoundException(Step.class));

        Case case1 = DTO_TO_CASE.apply(caseDto);
        Case savedCase = this.caseService.createCaseForStepById(step, case1);

        URI location = UriComponentsBuilder
                .fromPath("api/cases/{caseId}")
                .buildAndExpand(Map.of("caseId", savedCase.getId()))
                .toUri();

        return ResponseEntity
                .created(location)
                .body(CASE_TO_DTO.apply(savedCase));
    }


    @DeleteMapping("/{stepId:\\d+}")
    public ResponseEntity<Void> deleteStepById(@PathVariable(name = "stepId") Long stepId,
                                               Principal principal) {
        Account account = SecurityUtil.getCurrentAccount(this.accountService, principal);
        this.stepService.checkOwnershipOrElseThrow(stepId, account.getId());

        this.stepService.deleteStepById(stepId);

        return ResponseEntity
                .noContent()
                .build();
    }


}

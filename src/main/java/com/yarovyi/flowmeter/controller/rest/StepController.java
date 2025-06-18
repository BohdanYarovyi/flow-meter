package com.yarovyi.flowmeter.controller.rest;

import com.yarovyi.flowmeter.entity.account.Account;
import com.yarovyi.flowmeter.entity.flow.Case;
import com.yarovyi.flowmeter.entity.flow.Step;
import com.yarovyi.flowmeter.dto.flow.CaseDto;
import com.yarovyi.flowmeter.dto.flow.StepDto;
import com.yarovyi.flowmeter.exception.SubentityNotFoundException;
import com.yarovyi.flowmeter.service.AccountService;
import com.yarovyi.flowmeter.service.CaseService;
import com.yarovyi.flowmeter.service.StepService;
import com.yarovyi.flowmeter.util.SecurityUtil;
import com.yarovyi.flowmeter.mapper.StepMapper;
import com.yarovyi.flowmeter.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.Map;

import static com.yarovyi.flowmeter.mapper.CaseMapper.CASE_TO_DTO;
import static com.yarovyi.flowmeter.mapper.CaseMapper.DTO_TO_CASE;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/steps")
public class StepController {
    private final AccountService accountService;
    private final StepService stepService;
    private final CaseService caseService;


    @GetMapping("/{stepId:\\d+}")
    public ResponseEntity<StepDto> getById(@PathVariable(name = "stepId") Long stepId) {
        Step step = this.stepService
                .getStepById(stepId)
                .orElseThrow(() -> new SubentityNotFoundException(Step.class));

        StepDto dto = StepMapper.STEP_TO_DTO.apply(step);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{stepId:\\d+}/target-percentage")
    public ResponseEntity<Map<String, String>> getTargetPercentage(@PathVariable(name = "stepId") Long stepId) {
        Long targetPercentage = this.stepService.getTargetPercentageByStepId(stepId);

        var response = Map.of("targetPercentage", String.valueOf(targetPercentage));
        return ResponseEntity.ok(response);
    }


    @PostMapping("/{stepId:\\d+}/cases")
    public ResponseEntity<CaseDto> createCaseForStep(@PathVariable(name = "stepId") Long stepId,
                                                     @RequestBody @Validated CaseDto caseDto,
                                                     BindingResult bindingResult,
                                                     Principal principal) {
        ValidationUtil.checkOrThrow(bindingResult);

        Account account = SecurityUtil.getCurrentAccount(this.accountService, principal);
        this.stepService.checkOwnership(stepId, account.getId());

        Step step = this.stepService
                .getStepById(stepId)
                .orElseThrow(() -> new SubentityNotFoundException(Step.class));

        Case case1 = DTO_TO_CASE.apply(caseDto);
        Case savedCase = this.caseService.createCaseForStep(step, case1);

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

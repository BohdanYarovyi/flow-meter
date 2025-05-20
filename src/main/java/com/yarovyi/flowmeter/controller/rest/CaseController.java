package com.yarovyi.flowmeter.controller.rest;

import com.yarovyi.flowmeter.entity.account.Account;
import com.yarovyi.flowmeter.entity.flow.Case;
import com.yarovyi.flowmeter.dto.flow.CaseDto;
import com.yarovyi.flowmeter.service.AccountService;
import com.yarovyi.flowmeter.service.CaseService;
import com.yarovyi.flowmeter.util.SecurityUtil;
import com.yarovyi.flowmeter.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

import static com.yarovyi.flowmeter.mapper.CaseMapper.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/cases")
public class CaseController {
    private final AccountService accountService;
    private final CaseService caseService;


    @PutMapping
    public ResponseEntity<CaseDto> editCase(@RequestBody @Validated CaseDto caseDto,
                                            BindingResult bindingResult,
                                            Principal principal) {
        ValidationUtil.checkOrThrow(bindingResult);

        Account account = SecurityUtil.getCurrentAccount(this.accountService, principal);
        this.caseService.checkOwnershipOrElseThrow(caseDto.id(), account.getId());

        Case updatedCase = this.caseService.edit(DTO_TO_CASE.apply(caseDto));

        return ResponseEntity
                .ok(CASE_TO_DTO.apply(updatedCase));
    }


    @DeleteMapping("/{caseId:\\d+}")
    public ResponseEntity<Void> deleteCaseById(@PathVariable(value = "caseId") Long caseId,
                                               Principal principal) {
        Account account = SecurityUtil.getCurrentAccount(this.accountService, principal);
        this.caseService.checkOwnershipOrElseThrow(caseId, account.getId());

        this.caseService.deleteCaseById(caseId);

        return ResponseEntity
                .noContent()
                .build();
    }


}

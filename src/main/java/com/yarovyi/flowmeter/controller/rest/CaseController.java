package com.yarovyi.flowmeter.controller.rest;

import com.yarovyi.flowmeter.domain.account.Account;
import com.yarovyi.flowmeter.domain.flow.Case;
import com.yarovyi.flowmeter.entity.dto.CaseDto;
import com.yarovyi.flowmeter.service.AccountService;
import com.yarovyi.flowmeter.service.CaseService;
import com.yarovyi.flowmeter.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

import static com.yarovyi.flowmeter.util.CaseMapper.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/cases")
public class CaseController {
    private final AccountService accountService;
    private final CaseService caseService;


    @PutMapping
    public ResponseEntity<CaseDto> editCase(@RequestBody CaseDto caseDto,
                                            Principal principal) {
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

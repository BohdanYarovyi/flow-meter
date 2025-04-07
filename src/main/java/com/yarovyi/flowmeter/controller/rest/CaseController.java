package com.yarovyi.flowmeter.controller.rest;

import com.yarovyi.flowmeter.domain.account.Account;
import com.yarovyi.flowmeter.domain.flow.Case;
import com.yarovyi.flowmeter.entity.dto.CaseDto;
import com.yarovyi.flowmeter.entity.exception.AccountAuthenticationException;
import com.yarovyi.flowmeter.service.AccountService;
import com.yarovyi.flowmeter.service.CaseService;
import com.yarovyi.flowmeter.util.CaseMapper;
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
        Account currentAccount = this.accountService
                .getAccountByLogin(principal.getName())
                .orElseThrow(() -> new AccountAuthenticationException("Account is not logged in"));

        // check
        this.caseService.getCaseByIdAndAccountId(caseDto.id(), currentAccount.getId());

        Case updatedCase = this.caseService.edit(DTO_TO_CASE.apply(caseDto));

        return ResponseEntity
                .ok(CASE_TO_DTO.apply(updatedCase));
    }

}

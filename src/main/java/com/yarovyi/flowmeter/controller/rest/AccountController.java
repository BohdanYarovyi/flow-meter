package com.yarovyi.flowmeter.controller.rest;

import com.yarovyi.flowmeter.domain.account.Account;
import com.yarovyi.flowmeter.domain.flow.Flow;
import com.yarovyi.flowmeter.entity.dto.AccountDto;
import com.yarovyi.flowmeter.entity.dto.AccountUpdatedDto;
import com.yarovyi.flowmeter.entity.dto.FlowDto;
import com.yarovyi.flowmeter.entity.exception.AccountAuthenticationException;
import com.yarovyi.flowmeter.entity.exception.ForbiddenRequestException;
import com.yarovyi.flowmeter.entity.exception.SubentityNotFoundException;
import com.yarovyi.flowmeter.service.AccountService;
import com.yarovyi.flowmeter.service.FlowService;
import com.yarovyi.flowmeter.util.FlowMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.yarovyi.flowmeter.util.AccountMapper.*;
import static com.yarovyi.flowmeter.util.FlowMapper.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    private final AccountService accountService;
    private final FlowService flowService;


    @GetMapping
    public ResponseEntity<List<AccountDto>> getAccounts() {
        List<AccountDto> accountDtos = ACCOUNTs_TO_DTOs.apply(this.accountService.getAccounts());

        return ResponseEntity.ok(accountDtos);
    }


    @GetMapping("/current")
    public ResponseEntity<?> getCurrentAccount(Principal principal) {
        Optional<Account> optional = accountService.getAccountByLogin(principal.getName());

        if (optional.isPresent()) {
            Long id = optional.get().getId();
            return ResponseEntity.ok(Map.of("currentAccountId", id));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


    @GetMapping("/{accountId:\\d+}")
    public ResponseEntity<?> getAccountById(@PathVariable(name = "accountId") Long accountId) {
        Account account = this.accountService.getAccountById(accountId).orElseThrow(
                () -> new SubentityNotFoundException("Account with id:%s not found".formatted(accountId), Account.class)
        );

        AccountDto dto = ACCOUNT_TO_DTO.apply(account);

        return ResponseEntity.ok(dto);
    }


    @PutMapping
    public ResponseEntity<Void> updateAccount(@RequestBody AccountUpdatedDto updatedAccount) {
        Account account = this.accountService.getAccountById(updatedAccount.id()).orElseThrow(
                () -> new SubentityNotFoundException("Account with id:%s not found".formatted(updatedAccount.id()), Account.class)
        );

        Account commitedAccount = COMMIT_ACCOUNT_UPDATES.apply(updatedAccount, account);
        this.accountService.updateAccount(commitedAccount);

        return ResponseEntity.noContent().build();
    }


    @DeleteMapping("/{accountId:\\d+}")
    public ResponseEntity<Void> deleteAccount(@PathVariable(name = "accountId") Long id) {
        this.accountService.deleteAccountById(id);

        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{accountId:\\d+}/flows")
    public ResponseEntity<List<FlowDto>> getFlowsByAccountId(@PathVariable(name = "accountId") Long accountId) {
        List<Flow> flows = this.flowService.getAllByAccountId(accountId);

        List<FlowDto> dtos = FLOWs_TO_DTOs.apply(flows);

        return ResponseEntity.ok(dtos);
    }


    @PostMapping("/{accountId:\\d+}/flows")
    public ResponseEntity<FlowDto> createFlowForAccount(@PathVariable(name = "accountId") Long accountId,
                                                     @RequestBody FlowDto flowDto,
                                                     Principal principal) {
        Account currentAccount = this.accountService.getAccountByLogin(principal.getName())
                .orElseThrow(() -> new AccountAuthenticationException("Account is not logged in"));

        if (!Objects.equals(currentAccount.getId(), accountId))
            throw new ForbiddenRequestException("Creating flow forbidden", "Account cannot create flow for other accounts");

        Flow flow = DTO_TO_FLOW.apply(flowDto);
        Flow savedFlow = this.flowService.createFlowForAccount(flow, currentAccount);

        URI location = UriComponentsBuilder
                .fromPath("/api/flows/{flowId}")
                .buildAndExpand(Map.of("flowId", savedFlow.getId()))
                .toUri();

        return ResponseEntity.created(location)
                .body(FLOW_TO_DTO.apply(savedFlow));
    }


}

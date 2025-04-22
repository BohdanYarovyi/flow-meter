package com.yarovyi.flowmeter.controller.rest;

import com.yarovyi.flowmeter.domain.account.Account;
import com.yarovyi.flowmeter.domain.account.Credentials;
import com.yarovyi.flowmeter.domain.account.PersonalInfo;
import com.yarovyi.flowmeter.domain.flow.Flow;
import com.yarovyi.flowmeter.entity.domainDto.AccountDto;
import com.yarovyi.flowmeter.entity.domainDto.CredentialsDto;
import com.yarovyi.flowmeter.entity.domainDto.PersonalInfoDto;
import com.yarovyi.flowmeter.entity.domainDto.FlowDto;
import com.yarovyi.flowmeter.entity.exception.ForbiddenRequestException;
import com.yarovyi.flowmeter.entity.exception.SubentityNotFoundException;
import com.yarovyi.flowmeter.entity.securityDto.PasswordChangeRequest;
import com.yarovyi.flowmeter.service.AccountService;
import com.yarovyi.flowmeter.service.FlowService;
import com.yarovyi.flowmeter.service.SecurityService;
import com.yarovyi.flowmeter.util.SecurityUtil;
import com.yarovyi.flowmeter.util.ValidationUtil;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
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
    private final SecurityService securityService;
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
            return ResponseEntity
                    .ok(Map.of("currentAccountId", id));
        } else {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }
    }


    @GetMapping("/{accountId:\\d+}")
    public ResponseEntity<?> getAccountById(@PathVariable(name = "accountId") Long accountId) {
        Account account = this.accountService
                .getAccountById(accountId)
                .orElseThrow(() -> {
                    var message = String.format("Account with id:%s not found", accountId);
                    return new SubentityNotFoundException(message, Account.class);
                });

        AccountDto dto = ACCOUNT_TO_DTO.apply(account);

        return ResponseEntity.ok(dto);
    }


    @PutMapping("/{accountId:\\d+}/edit/personal-info")
    public ResponseEntity<Void> updatePersonalInfo(@PathVariable(name = "accountId") Long accountId,
                                                   @RequestBody PersonalInfoDto updatedInfo,
                                                   Principal principal) {
        Account currentAccount = SecurityUtil.getCurrentAccount(this.accountService, principal);
        this.accountService.checkOwnershipOrElseThrow(currentAccount.getId(), accountId);

        PersonalInfo personalInfo = DTO_TO_PERSONAL_INFO.apply(updatedInfo);
        this.accountService.updatePersonalInfo(currentAccount, personalInfo);

        return ResponseEntity
                .noContent()
                .build();
    }


    @PutMapping("/{accountId:\\d+}/edit/credentials")
    public ResponseEntity<Void> updateCredentials(@PathVariable(name = "accountId") Long accountId,
                                                  @RequestBody @Validated CredentialsDto updatedCredentials,
                                                  BindingResult bindingResult,
                                                  Principal principal,
                                                  HttpSession session) {
        ValidationUtil.checkOrThrow(bindingResult);

        Account currentAccount = SecurityUtil.getCurrentAccount(this.accountService, principal);
        this.accountService.checkOwnershipOrElseThrow(currentAccount.getId(), accountId);

        Credentials credentials = DTO_TO_CREDENTIALS.apply(updatedCredentials);
        Account account = this.accountService.updateCredentials(currentAccount, credentials);
        this.securityService.reauthenticate(account, session);

        return ResponseEntity
                .noContent()
                .build();
    }


    // todo: validate PasswordChangeRequest obj
    @PutMapping("/{accountId:\\d+}/edit/password")
    public ResponseEntity<Void> updatePassword(@PathVariable(name = "accountId") Long accountId,
                                               @RequestBody @Validated PasswordChangeRequest passwordChangeRequest,
                                               BindingResult bindingResult,
                                               Principal principal,
                                               HttpSession session) {
        ValidationUtil.checkOrThrow(bindingResult);

        Account currentAccount = SecurityUtil.getCurrentAccount(this.accountService, principal);
        this.accountService.checkOwnershipOrElseThrow(currentAccount.getId(), accountId);

        this.accountService.changePassword(currentAccount, passwordChangeRequest);
        this.securityService.deauthenticate(currentAccount, session);

        return ResponseEntity
                .noContent()
                .build();
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
                                                        @RequestBody @Validated FlowDto flowDto,
                                                        BindingResult bindingResult,
                                                        Principal principal) {
        ValidationUtil.checkOrThrow(bindingResult);

        Account account = SecurityUtil.getCurrentAccount(this.accountService, principal);
        if (!Objects.equals(account.getId(), accountId)) {
            var title = "Creating flow forbidden";
            var message = "Account cannot create flow for other accounts";

            throw new ForbiddenRequestException(title, message);
        }

        Flow flow = DTO_TO_FLOW.apply(flowDto);
        Flow savedFlow = this.flowService.createFlowForAccount(flow, account);

        URI location = UriComponentsBuilder
                .fromPath("/api/flows/{flowId}")
                .buildAndExpand(Map.of("flowId", savedFlow.getId()))
                .toUri();

        return ResponseEntity
                .created(location)
                .body(FLOW_TO_DTO.apply(savedFlow));
    }


}

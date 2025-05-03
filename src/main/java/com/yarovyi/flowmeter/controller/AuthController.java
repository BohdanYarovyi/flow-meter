package com.yarovyi.flowmeter.controller;

import com.yarovyi.flowmeter.domain.account.Account;
import com.yarovyi.flowmeter.entity.domainDto.AccountCreatedDto;
import com.yarovyi.flowmeter.entity.securityDto.LoginRequest;
import com.yarovyi.flowmeter.entity.securityDto.LoginResponse;
import com.yarovyi.flowmeter.service.AccountService;
import com.yarovyi.flowmeter.service.SecurityService;
import com.yarovyi.flowmeter.util.ValidationUtil;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

import static com.yarovyi.flowmeter.util.AccountMapper.ACCOUNT_CREATED_DTO_TO_ACCOUNT;

@RequiredArgsConstructor
@Controller
public class AuthController {
    private final SecurityService securityService;
    private final AccountService accountService;


    @GetMapping("/login")
    public String getLogin() {
        return "pub/login";
    }


    @GetMapping("/registration")
    public String getRegistration() {
        return "pub/registration";
    }


    @PostMapping("/api/public/login")
    @ResponseBody
    public ResponseEntity<LoginResponse> login(@RequestBody @Validated LoginRequest request,
                                               BindingResult bindingResult,
                                               HttpSession session) {   // todo: session for removing from here
        ValidationUtil.checkOrThrow(bindingResult);

        this.securityService.loginByUsernamePassword(request, session);

        return ResponseEntity.ok(new LoginResponse("Login success"));
    }


    @PostMapping("/api/public/registration")
    @ResponseBody
    public ResponseEntity<Void> register(@RequestBody @Validated AccountCreatedDto registerDto,
                                         BindingResult bindingResult) {
        ValidationUtil.checkOrThrow(bindingResult);

        Account forRegister = ACCOUNT_CREATED_DTO_TO_ACCOUNT.apply(registerDto);
        Long accountId = this.securityService.register(forRegister, this.accountService);

        URI location = UriComponentsBuilder
                .fromPath("/api/accounts/{id}")
                .buildAndExpand(Map.of("id", accountId))
                .toUri();

        return ResponseEntity.created(location).build();
    }


}

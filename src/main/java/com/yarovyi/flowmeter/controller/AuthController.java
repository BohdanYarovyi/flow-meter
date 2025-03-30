package com.yarovyi.flowmeter.controller;

import com.yarovyi.flowmeter.domain.account.Account;
import com.yarovyi.flowmeter.entity.dto.AccountCreatedDto;
import com.yarovyi.flowmeter.entity.exception.AccountAuthenticationException;
import com.yarovyi.flowmeter.entity.exception.EntityValidationException;
import com.yarovyi.flowmeter.entity.login.LoginRequest;
import com.yarovyi.flowmeter.entity.login.LoginResponse;
import com.yarovyi.flowmeter.service.AccountService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
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
    private final AuthenticationProvider authenticationProvider;
    private final AccountService accountService;


    @GetMapping("/login")
    public String getLogin() {
        return "public/login";
    }


    @GetMapping("/registration")
    public String getRegistration() {
        return "public/registration";
    }


    @PostMapping("/api/public/login")
    @ResponseBody
    public ResponseEntity<LoginResponse> login(@RequestBody @Validated LoginRequest request,
                                               BindingResult bindingResult,
                                               HttpServletRequest httpRequest) {
        if (bindingResult.hasErrors()) {
            throw new EntityValidationException(bindingResult);
        }

        try {
            var authToken = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
            Authentication authentication = authenticationProvider.authenticate(authToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            HttpSession session = httpRequest.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

            return ResponseEntity.ok(new LoginResponse("Login success"));
        } catch (AuthenticationException e) {
            throw new AccountAuthenticationException("Invalid username or password");
        }
    }


    @PostMapping("/api/public/registration")
    @ResponseBody
    public ResponseEntity<Void> register(@RequestBody @Validated AccountCreatedDto registerDto,
                                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new EntityValidationException(bindingResult);
        }

        Account newAccount = ACCOUNT_CREATED_DTO_TO_ACCOUNT.apply(registerDto);
        Long accountId = this.accountService.createAccount(newAccount);

        URI location = UriComponentsBuilder
                .fromPath("/api/accounts/{id}")
                .buildAndExpand(Map.of("id", accountId))
                .toUri();

        return ResponseEntity.created(location).build();
    }


}

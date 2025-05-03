package com.yarovyi.flowmeter.service;

import com.yarovyi.flowmeter.domain.account.Account;
import com.yarovyi.flowmeter.domain.account.Role;
import com.yarovyi.flowmeter.entity.exception.AccountAuthenticationException;
import com.yarovyi.flowmeter.entity.securityDto.LoginRequest;
import com.yarovyi.flowmeter.util.SecurityUtil;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Objects;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.*;

@RequiredArgsConstructor
@Service
public class SecurityServiceImpl implements SecurityService {
    private final AuthenticationManager authenticationManager;

    @Override
    public void loginByUsernamePassword(LoginRequest loginRequest, HttpSession session) {
        if (Objects.isNull(loginRequest) || Objects.isNull(session))
            throw new NullPointerException("LoginRequest or session is null in parameters");

        try {
            var authToken = new UsernamePasswordAuthenticationToken(
                    loginRequest.username(),
                    loginRequest.password()
            );
            Authentication authentication = this.authenticate(authToken);

            this.setAuthenticationInSession(authentication, session);
            this.setAuthenticationInContextHolder(authentication);
        } catch (AuthenticationException e) {
            throw new AccountAuthenticationException("Invalid username or password");
        }
    }

    private void setAuthenticationInSession(Authentication authentication, HttpSession session) {
        SecurityContext securityContext = (SecurityContext) session.getAttribute(SPRING_SECURITY_CONTEXT_KEY);

        if (securityContext == null) {
            securityContext = SecurityContextHolder.createEmptyContext();
            session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, securityContext);
        }

        securityContext.setAuthentication(authentication);
    }

    private void setAuthenticationInContextHolder(Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    // i don't know, for what this wrapper here
    // but maybe in future it will be useful
    // maybe i will want to make login at the registration moment
    @Override
    public Long register(Account account, AccountService accountService) {
        return accountService.createAccount(account);
    }

    @Override
    public void reauthenticate(Account account, HttpSession session) {
        var username = account.getCredentials().getLogin();
        var authorities = account.getRoles().stream()
                .map(Role::getName)
                .map(SecurityUtil.PERFORM_WITH_ROLE_PREFIX)
                .map(SimpleGrantedAuthority::new)
                .toList();

        Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);
        this.setAuthenticationInSession(authentication, session);
        this.setAuthenticationInContextHolder(authentication);
    }

    @Override
    public void deauthenticate(Account account, HttpSession session) {
        SecurityContext securityContext = (SecurityContext) session.getAttribute(SPRING_SECURITY_CONTEXT_KEY);
        if (securityContext != null) {
            securityContext.setAuthentication(null);
        }

        SecurityContextHolder.clearContext();
    }

    public Authentication authenticate(Authentication authentication) {
        return this.authenticationManager.authenticate(authentication);
    }

}

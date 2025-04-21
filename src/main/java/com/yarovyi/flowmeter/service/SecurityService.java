package com.yarovyi.flowmeter.service;

import com.yarovyi.flowmeter.domain.account.Account;
import com.yarovyi.flowmeter.entity.securityDto.LoginRequest;
import jakarta.servlet.http.HttpSession;

public interface SecurityService {
    void login(LoginRequest loginRequest, HttpSession session);
    Long register(Account account, AccountService accountService);
    void reauthenticate(Account account, HttpSession session);
    void deauthenticate(Account account, HttpSession session);
}

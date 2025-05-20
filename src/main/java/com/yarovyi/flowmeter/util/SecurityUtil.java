package com.yarovyi.flowmeter.util;

import com.yarovyi.flowmeter.entity.account.Account;
import com.yarovyi.flowmeter.exception.AccountAuthenticationException;
import com.yarovyi.flowmeter.service.AccountService;

import java.security.Principal;
import java.util.function.Function;

public class SecurityUtil {

    public static final Function<String, String> PERFORM_WITH_ROLE_PREFIX = (nameOfRole) -> "ROLE_" + nameOfRole;


    public static Account getCurrentAccount(AccountService accountService, Principal principal) {
        return accountService
                .getAccountByLogin(principal.getName())
                .orElseThrow(() -> new AccountAuthenticationException("Account is not logged in"));
    }


}

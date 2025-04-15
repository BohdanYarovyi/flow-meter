package com.yarovyi.flowmeter.service;

import com.yarovyi.flowmeter.domain.account.Account;

import java.util.List;
import java.util.Optional;

public interface AccountService {
    List<Account> getAccounts();
    Optional<Account> getAccountById(Long accountId);
    Optional<Account> getAccountByLogin(String login);
    Optional<Account> getAccountByEmail(String email);
    Long createAccount(Account account);
    void updatePersonalInfo(Account account);
    void deleteAccountById(Long id);

    boolean checkOwnership(Long currentAccountId, Long targetAccountId);
    void checkOwnershipOrElseThrow(Long currentAccountId, Long targetAccountId);
}

package com.yarovyi.flowmeter.service;

import com.yarovyi.flowmeter.entity.account.Account;
import com.yarovyi.flowmeter.entity.account.Credential;
import com.yarovyi.flowmeter.entity.account.PersonalInfo;
import com.yarovyi.flowmeter.dto.auth.PasswordChangeRequest;
import com.yarovyi.flowmeter.entity.account.Role;

import java.util.List;
import java.util.Optional;

public interface AccountService {
    List<Account> getAccounts();
    Optional<Account> getAccountById(Long accountId);
    Optional<Account> getAccountByLogin(String login);
    Optional<Account> getAccountByEmail(String email);
    Long createAccount(Account account);
    Account createAndGetAccount(Account account);
    void updatePersonalInfo(Account account, PersonalInfo personalInfo);
    Account updateCredentials(Account account, Credential credential);
    void changePassword(Account account, PasswordChangeRequest passwordChangeRequest);
    void deleteAccountById(Long id);

    boolean existAccountByLogin(String login);
    boolean checkOwnership(Long currentAccountId, Long targetAccountId);
    void checkOwnershipOrElseThrow(Long currentAccountId, Long targetAccountId);
}

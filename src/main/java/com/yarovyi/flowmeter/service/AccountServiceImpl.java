package com.yarovyi.flowmeter.service;

import com.yarovyi.flowmeter.domain.account.Account;
import com.yarovyi.flowmeter.domain.account.Credentials;
import com.yarovyi.flowmeter.domain.account.PersonalInfo;
import com.yarovyi.flowmeter.domain.account.Role;
import com.yarovyi.flowmeter.domain.flow.Flow;
import com.yarovyi.flowmeter.entity.exception.AccountAuthenticationException;
import com.yarovyi.flowmeter.entity.exception.SubentityNotFoundException;
import com.yarovyi.flowmeter.entity.securityDto.PasswordChangeRequest;
import com.yarovyi.flowmeter.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.yarovyi.flowmeter.util.AccountMapper.COMMIT_CREDENTIALS_UPDATES;
import static com.yarovyi.flowmeter.util.AccountMapper.COMMIT_PERSONAL_INFO_UPDATES;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final Role defaultRole;


    @Override
    public List<Account> getAccounts() {
        return this.accountRepository.findAll();
    }


    @Override
    public Optional<Account> getAccountById(Long accountId) {
        if (Objects.isNull(accountId)) {
            throw new NullPointerException("AccountId is required");
        }

        return this.accountRepository.findById(accountId);
    }


    @Override
    public Optional<Account> getAccountByLogin(String login) {
        if (Objects.isNull(login)) {
            throw new NullPointerException("Login is required");
        }

        return this.accountRepository.findAccountByCredentialsLogin(login);
    }


    @Override
    public Optional<Account> getAccountByEmail(String email) {
        if (Objects.isNull(email)) {
            throw new NullPointerException("Email is required");
        }

        return this.accountRepository.findAccountByCredentialsEmail(email);
    }


    @Override
    public Long createAccount(Account account) {
        if (Objects.isNull(account))
            throw new NullPointerException("Account is required");

        if (!Objects.isNull(account.getId()))
            throw new IllegalArgumentException("Account.id must be null");

        String encodedPassword = passwordEncoder.encode(account.getCredentials().getPassword());
        account.getCredentials().setPassword(encodedPassword);
        account.getRoles().add(defaultRole);

        return this.accountRepository.save(account).getId();
    }


    @Override
    public void updatePersonalInfo(Account account, PersonalInfo personalInfo) {
        Account updatedAccount = COMMIT_PERSONAL_INFO_UPDATES.apply(personalInfo, account);
        this.accountRepository.save(updatedAccount);
    }


    @Override
    public Account updateCredentials(Account account, Credentials credentials) {
        // TODO: here is a unique fields like: login, email.
        //  I must write some handler for catching SQL Exceptions or another way
        Account updatedAccount = COMMIT_CREDENTIALS_UPDATES.apply(credentials, account);
        return this.accountRepository.save(updatedAccount);
    }


    @Override
    public void changePassword(Account account, PasswordChangeRequest passwordChangeRequest) {
        String rawCurrentPassword = passwordChangeRequest.currentPassword();
        String newPassword = passwordChangeRequest.newPassword();

        if (!this.passwordEncoder.matches(rawCurrentPassword, account.getCredentials().getPassword())) {
            throw new AccountAuthenticationException("Password don't match with current password");
        }

        if (this.passwordEncoder.matches(newPassword, account.getCredentials().getPassword())) {
            throw new AccountAuthenticationException("You can't change password to the same password");
        }

        String encoded = this.passwordEncoder.encode(newPassword);
        account.getCredentials().setPassword(encoded);

        this.accountRepository.save(account);
    }


    @Override
    public void deleteAccountById(Long id) {
        if (Objects.isNull(id))
            throw new NullPointerException("Id is required");

        Account account = this.accountRepository.findById(id).orElseThrow(
                () -> new SubentityNotFoundException("Account with id:%s not found".formatted(id), Account.class)
        );

        account.setDeleted(true);
        this.accountRepository.save(account);
    }


    @Override
    public boolean checkOwnership(Long currentAccountId, Long targetAccountId) {
        if (Objects.isNull(currentAccountId) || Objects.isNull(targetAccountId))
            throw new NullPointerException("Parameters is null");

        return Objects.equals(currentAccountId, targetAccountId);
    }


    @Override
    public void checkOwnershipOrElseThrow(Long currentAccountId, Long targetAccountId) {
        if (!this.checkOwnership(currentAccountId, targetAccountId)) {
            String message = "You can chane only your account";
            throw new SubentityNotFoundException(message, Account.class);
        }
    }


}

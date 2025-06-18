package com.yarovyi.flowmeter.service;

import com.yarovyi.flowmeter.entity.account.Account;
import com.yarovyi.flowmeter.entity.account.Credential;
import com.yarovyi.flowmeter.entity.account.PersonalInfo;
import com.yarovyi.flowmeter.entity.account.Role;
import com.yarovyi.flowmeter.exception.AccountAuthenticationException;
import com.yarovyi.flowmeter.exception.SubentityNotFoundException;
import com.yarovyi.flowmeter.dto.auth.PasswordChangeRequest;
import com.yarovyi.flowmeter.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.yarovyi.flowmeter.mapper.AccountMapper.COMMIT_CREDENTIALS_UPDATES;
import static com.yarovyi.flowmeter.mapper.AccountMapper.COMMIT_PERSONAL_INFO_UPDATES;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final Role defaultRole;


    @Transactional(readOnly = true)
    @Override
    public List<Account> getAccounts() {
        return this.accountRepository.findAll();
    }


    @Transactional(readOnly = true)
    @Override
    public Optional<Account> getAccountById(Long accountId) {
        if (Objects.isNull(accountId)) {
            throw new NullPointerException("AccountId is required");
        }

        return this.accountRepository.findById(accountId);
    }


    @Transactional(readOnly = true)
    @Override
    public Optional<Account> getAccountByLogin(String login) {
        if (Objects.isNull(login)) {
            throw new NullPointerException("Login is required");
        }

        return this.accountRepository.findAccountByCredentialLogin(login);
    }


    @Transactional(readOnly = true)
    @Override
    public Optional<Account> getAccountByEmail(String email) {
        if (Objects.isNull(email)) {
            throw new NullPointerException("Email is required");
        }

        return this.accountRepository.findAccountByCredentialEmail(email);
    }


    @Transactional
    @Override
    public Long createAccount(Account account) {
        return this.createAndGetAccount(account).getId();
    }


    @Transactional
    @Override
    public Account createAndGetAccount(Account account) {
        if (Objects.isNull(account))
            throw new NullPointerException("Account is required");

        if (!Objects.isNull(account.getId()))
            throw new IllegalArgumentException("Account.id must be null");

        String encodedPassword = this.passwordEncoder.encode(account.getCredential().getPassword());
        account.getCredential().setPassword(encodedPassword);
        account.getRoles().add(this.defaultRole);

        return this.accountRepository.save(account);
    }


    @Transactional
    @Override
    public void updatePersonalInfo(Account account, PersonalInfo personalInfo) {
        Account updatedAccount = COMMIT_PERSONAL_INFO_UPDATES.apply(personalInfo, account);
        this.accountRepository.save(updatedAccount);
    }


    @Transactional
    @Override
    public Account updateCredentials(Account account, Credential credential) {
        Account updatedAccount = COMMIT_CREDENTIALS_UPDATES.apply(credential, account);
        return this.accountRepository.save(updatedAccount);
    }


    @Transactional
    @Override
    public void changePassword(Account account, PasswordChangeRequest passwordChangeRequest) {
        String rawCurrentPassword = passwordChangeRequest.currentPassword();
        String newPassword = passwordChangeRequest.newPassword();

        if (!this.passwordEncoder.matches(rawCurrentPassword, account.getCredential().getPassword())) {
            throw new AccountAuthenticationException("Password don't match with current password");
        }

        if (this.passwordEncoder.matches(newPassword, account.getCredential().getPassword())) {
            throw new AccountAuthenticationException("You can't change password to the same password");
        }

        String encoded = this.passwordEncoder.encode(newPassword);
        account.getCredential().setPassword(encoded);

        this.accountRepository.save(account);
    }


    @Transactional
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


    @Transactional(readOnly = true)
    @Override
    public boolean existAccountByLogin(String login) {
        if (Objects.isNull(login))
            throw new NullPointerException("Login is required");

        return this.accountRepository.existsAccountByCredential_Login(login);
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

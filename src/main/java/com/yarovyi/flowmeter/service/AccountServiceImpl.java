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
        return accountRepository.findAll();
    }


    @Transactional(readOnly = true)
    @Override
    public Optional<Account> getAccountById(Long accountId) {
        if (Objects.isNull(accountId)) {
            throw new IllegalArgumentException("Parameter 'accountId' is null");
        }

        return accountRepository.findById(accountId);
    }


    @Transactional(readOnly = true)
    @Override
    public Optional<Account> getAccountByLogin(String login) {
        if (Objects.isNull(login)) {
            throw new IllegalArgumentException("Parameter 'login' is null");
        }

        return accountRepository.findAccountByCredentialLogin(login);
    }


    @Transactional(readOnly = true)
    @Override
    public Optional<Account> getAccountByEmail(String email) {
        if (Objects.isNull(email)) {
            throw new IllegalArgumentException("Parameter 'email' is null");
        }

        return accountRepository.findAccountByCredentialEmail(email);
    }


    @Transactional
    @Override
    public Long createAccount(Account account) {
        return this.createAndGetAccount(account).getId();
    }


    @Transactional
    @Override
    public Account createAndGetAccount(Account account) {
        if (account == null) {
            throw new IllegalArgumentException("Parameter 'account' is null");
        }

        if (account.getId() != null) {
            throw new IllegalArgumentException("Account.id must be null");
        }

        String encodedPassword = passwordEncoder.encode(account.getCredential().getPassword());
        account.getCredential().setPassword(encodedPassword);
        account.getRoles().add(defaultRole);

        return accountRepository.save(account);
    }


    @Transactional
    @Override
    public void updatePersonalInfo(Account account, PersonalInfo personalInfo) {
        if (account == null || personalInfo == null) {
            throw new IllegalArgumentException("Parameters 'account' or 'personalInfo' are null");
        }

        Account updatedAccount = COMMIT_PERSONAL_INFO_UPDATES.apply(personalInfo, account);
        accountRepository.save(updatedAccount);
    }


    @Transactional
    @Override
    public Account updateCredentials(Account account, Credential credential) {
        if (account == null || credential == null) {
            throw new IllegalArgumentException("Parameters 'account' or 'credential' are null");
        }

        if (account.getCredential() == null) {
            throw new IllegalArgumentException("Account.credential is null");
        }

        Account updatedAccount = COMMIT_CREDENTIALS_UPDATES.apply(credential, account);
        return accountRepository.save(updatedAccount);
    }


    @Transactional
    @Override
    public void changePassword(Account account, PasswordChangeRequest passwordChangeRequest) {
        if (account == null || passwordChangeRequest == null) {
            throw new IllegalArgumentException("Parameters 'account' or 'passwordChangeRequest' are null");
        }

        String rawCurrentPassword = passwordChangeRequest.currentPassword();
        String newPassword = passwordChangeRequest.newPassword();

        if (!passwordEncoder.matches(rawCurrentPassword, account.getCredential().getPassword())) {
            throw new AccountAuthenticationException("Password don't match with current password");
        }

        if (passwordEncoder.matches(newPassword, account.getCredential().getPassword())) {
            throw new AccountAuthenticationException("You can't change password to the same password");
        }

        String encoded = passwordEncoder.encode(newPassword);
        account.getCredential().setPassword(encoded);

        accountRepository.save(account);
    }


    @Transactional
    @Override
    public void deleteAccountById(Long id) {
        if (Objects.isNull(id)) {
            throw new IllegalArgumentException("Parameter 'id' is null");
        }

        Account account = accountRepository
                .findById(id)
                .orElseThrow(() -> new SubentityNotFoundException("Account with id:%s not found".formatted(id), Account.class));

        account.setDeleted(true);
        accountRepository.save(account);
    }


    @Transactional(readOnly = true)
    @Override
    public boolean existAccountByLogin(String login) {
        if (Objects.isNull(login)) {
            throw new IllegalArgumentException("Parameter 'login' is null");
        }

        return accountRepository.existsAccountByCredential_Login(login);
    }


    @Override
    public boolean checkOwnership(Long ownerId, Long targetId) {
        if (Objects.isNull(ownerId) || Objects.isNull(targetId)) {
            throw new IllegalArgumentException("Parameters 'ownerId' or 'targetId' are null");
        }

        return Objects.equals(ownerId, targetId);
    }


    @Override
    public void checkOwnershipOrElseThrow(Long ownerId, Long targetId) {
        if (!this.checkOwnership(ownerId, targetId)) {
            String message = "You can access only your account";
            throw new SubentityNotFoundException(message, Account.class);
        }
    }

}

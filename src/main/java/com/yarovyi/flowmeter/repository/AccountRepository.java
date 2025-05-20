package com.yarovyi.flowmeter.repository;

import com.yarovyi.flowmeter.entity.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account,Long> {
    Optional<Account> findAccountByCredentialLogin(String login);
    Optional<Account> findAccountByCredentialEmail(String email);

    boolean existsAccountByCredential_Login(String login);
}

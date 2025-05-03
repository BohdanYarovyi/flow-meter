package com.yarovyi.flowmeter.repository;

import com.yarovyi.flowmeter.domain.account.Account;
import com.yarovyi.flowmeter.domain.flow.Flow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account,Long> {
    Optional<Account> findAccountByCredentialsLogin(String login);
    Optional<Account> findAccountByCredentialsEmail(String email);

    boolean existsAccountByCredentials_Login(String login);
}

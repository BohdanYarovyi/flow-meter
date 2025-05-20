package com.yarovyi.flowmeter.security;

import com.yarovyi.flowmeter.entity.account.Account;
import com.yarovyi.flowmeter.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Function;

import static com.yarovyi.flowmeter.util.SecurityUtil.PERFORM_WITH_ROLE_PREFIX;

@Component
@RequiredArgsConstructor
public class DaoUserDetailsService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(DaoUserDetailsService.class);
    private static final Function<String, String> USERNAME_NOT_FOUND = "Username '%s' not found"::formatted;
    private final AccountService accountService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("Attempting to load user: {}", username);

        Optional<Account> accountByUsername;
        if (username.contains("@")) {
            accountByUsername = accountService.getAccountByEmail(username);
        } else {
            accountByUsername = accountService.getAccountByLogin(username);
        }

        Account account = accountByUsername
                .orElseThrow(() -> new UsernameNotFoundException(USERNAME_NOT_FOUND.apply(username)));

        var credentials = account.getCredential();
        var authorities = account.getRoles().stream()
                .map(r -> new SimpleGrantedAuthority(PERFORM_WITH_ROLE_PREFIX.apply(r.getName())))
                .toList();

        logger.info("User {} loaded successfully with roles: {}", username, authorities);
        return User.builder()
                .username(credentials.getLogin())
                .password(credentials.getPassword())
                .authorities(authorities)
                .build();
    }

}

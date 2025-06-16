package com.yarovyi.flowmeter.security.successHandler;

import com.yarovyi.flowmeter.entity.account.Account;
import com.yarovyi.flowmeter.dto.account.AccountCreatedDto;
import com.yarovyi.flowmeter.exception.AccountAuthenticationException;
import com.yarovyi.flowmeter.service.AccountService;
import com.yarovyi.flowmeter.service.NotificationService;
import com.yarovyi.flowmeter.service.SecurityService;
import com.yarovyi.flowmeter.util.AccountCreator;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static com.yarovyi.flowmeter.mapper.AccountMapper.ACCOUNT_CREATED_DTO_TO_ACCOUNT;

@Component
@RequiredArgsConstructor
public class OAuthSuccessHandler implements AuthenticationSuccessHandler {
    private final Logger LOG = LoggerFactory.getLogger(OAuthSuccessHandler.class);
    private final AccountService accountService;
    private final SecurityService securityService;
    private final NotificationService notificationService;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        response.sendRedirect("/");

        if (authentication instanceof OAuth2AuthenticationToken token) {
            HttpSession session = request.getSession();
            OAuth2User principal = token.getPrincipal();
            String email = (String) principal.getAttributes().get("email");
            Optional<Account> accountOptional = accountService.getAccountByEmail(email);

            if (accountOptional.isPresent()) {
                Account account = accountOptional.get();
                securityService.reauthenticate(account, session);
            } else {
                Account createdAccount = this.createAccountOfGoogle(principal);
                securityService.reauthenticate(createdAccount, session);
            }
        }
    }


    private Account createAccountOfGoogle(OAuth2User principal) {
        Map<String, Object> attributes = principal.getAttributes();

        String googleId = (String) attributes.get("sub");
        String email = (String) attributes.get("email");
        String firstname = (String) attributes.get("given_name");
        String lastname = (String) attributes.get("family_name");
        String login = findFreeLogin(firstname, lastname, googleId);
        String password = AccountCreator.generatePassword(16);

        AccountCreatedDto createdDto = AccountCreatedDto.baseAccount(login, email, password, firstname, lastname);
        Account toCreate = ACCOUNT_CREATED_DTO_TO_ACCOUNT.apply(createdDto);
        Account created = accountService.createAndGetAccount(toCreate);
        notificationService.sendMessageWithPassword(email, password);

        return created;
    }


    private String findFreeLogin(String firstname, String lastname, String googleId) {
        String login;
        boolean loginExists;
        int maxAttempt = 20;
        int attempt = 0;
        long shortLoginId = Long.parseLong(googleId.substring(10));

        do {
            login = AccountCreator.generateNewLogin(firstname, lastname, shortLoginId);
            loginExists = accountService.existAccountByLogin(login);
            attempt++;
        } while (loginExists && attempt < maxAttempt);

        if (loginExists) {
            String errorTmp = "Failed to find login for user: %s %s googleId: %s";
            LOG.error(errorTmp.formatted(firstname, lastname, googleId));

            throw new AccountAuthenticationException("Something was wrong, try again please!");
        }

        return login;
    }

}

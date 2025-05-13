package com.yarovyi.flowmeter.controller.successHandlers;

import com.yarovyi.flowmeter.domain.account.Account;
import com.yarovyi.flowmeter.domain.account.Credentials;
import com.yarovyi.flowmeter.domain.account.PersonalInfo;
import com.yarovyi.flowmeter.domain.account.Role;
import com.yarovyi.flowmeter.entity.domainDto.AccountCreatedDto;
import com.yarovyi.flowmeter.service.AccountService;
import com.yarovyi.flowmeter.service.SecurityService;
import com.yarovyi.flowmeter.util.AccountMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import static com.yarovyi.flowmeter.util.AccountMapper.ACCOUNT_CREATED_DTO_TO_ACCOUNT;

// example token fetch
/*
        at_hash          --------   rBtuxSEyH9SQN0twtMPtFw
        sub              --------   116279275092574045365
        email_verified   --------   true
        iss              --------   https://accounts.google.com
        given_name       --------   Bohdan
        nonce            --------   JtCARt8zbkyW6L8iSt54P2yjTCp3y9Vu7H3KJhrWw8o
        picture          --------   https://lh3.googleusercontent.com/a/ACg8ocJgCve4MtqCIcxsqPIwbF8clGxWwpGEPj10C0PWcpmUFnsXluEx=s96-c
        aud              --------   [955713173154-cgaq9k1d9dmbl71957t3of8mmhptgbj2.apps.googleusercontent.com]
        azp              --------   955713173154-cgaq9k1d9dmbl71957t3of8mmhptgbj2.apps.googleusercontent.com
        name             --------   Bohdan Yarovyi
        exp              --------   2025-05-02T12:09:17Z
        family_name      --------   Yarovyi
        iat              --------   2025-05-02T11:09:17Z
        email            --------   bogdan.yarovoy.01@gmail.com
*/

@Component
@RequiredArgsConstructor
public class OAuthSuccessHandler implements AuthenticationSuccessHandler {
    private final AccountService accountService;
    private final SecurityService securityService;

    // todo: після того, як я розлогінююсь, а потім намагаюсь зайти через OAuth,
    //  то в мене близько 30 секунд не може зайти чомусь
    //  може це якісь обмеження від гугла, хз. От жиди..
    //  А тепер все працює класно, це може бути пов'язано також з моїм інтернетом, поспостерігаю ще
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        response.sendRedirect("/");

        if (authentication instanceof OAuth2AuthenticationToken token) {
            HttpSession session = request.getSession();
            OAuth2User principal = token.getPrincipal();
            String email = (String) principal.getAttributes().get("email");
            Optional<Account> accountOptional = this.accountService.getAccountByEmail(email);

            if (accountOptional.isPresent()) {
                Account account = accountOptional.get();
                this.securityService.reauthenticate(account, session);
            } else {
                Account createdAccount = this.createAccountOfGoogle(principal);
                this.securityService.reauthenticate(createdAccount, session);
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
        String defaultPassword = "password"; // todo temporary: it is danger to create constantly such password for user

        AccountCreatedDto createdDto = new AccountCreatedDto(
                login,
                email,
                defaultPassword, // todo temporary: maybe it is ok to create password for user and send it on email
                firstname,
                lastname,
                null,
                null,
                null
        );
        Account account = ACCOUNT_CREATED_DTO_TO_ACCOUNT.apply(createdDto);

        return this.accountService.createAndGetAccount(account);
    }

    private String findFreeLogin(String firstname, String lastname, String googleId) {
        boolean loginExists;
        String login;

        long formattedLoginId = Long.parseLong(googleId.substring(10));
        do {
            login = generateNewLogin(firstname, lastname, formattedLoginId);
            loginExists = this.accountService.existAccountByLogin(login);
        } while (loginExists);  // maybe 20 attempts needed here, not infinity in theory

        return login;
    }

    private String generateNewLogin(String firstname, String lastname, long googleId) {
        String suggestedLoginTemplate = "%s_%s_%s";
        Random random = new Random();
        int randomBound = 200_000;

        long number = (googleId + random.nextInt(randomBound));
        return suggestedLoginTemplate.formatted(firstname, lastname, number);
    }

}

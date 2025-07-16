package com.yarovyi.flowmeter.service;

import com.yarovyi.flowmeter.dto.auth.LoginRequest;
import com.yarovyi.flowmeter.entity.account.Account;
import com.yarovyi.flowmeter.exception.AccountAuthenticationException;
import com.yarovyi.flowmeter.testUtility.entityGenerator.AccountGenerator;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

@ExtendWith(MockitoExtension.class)
class SecurityServiceImplTest {
    @Mock
    private AuthenticationManager authenticationManager;
    @InjectMocks
    private SecurityServiceImpl securityService;
    @Mock
    private HttpSession session;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        Mockito.reset(session);
    }


/*
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    loginByUsernamePassword(LoginRequest loginRequest, HttpSession session)
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
*/
    @Test
    void loginByUsernamePassword_whenItIsOk_thenAuthenticateInSession() {
        // given
        String username = "johndoe123";
        String password = "password123";
        LoginRequest loginRequest = new LoginRequest(username, password);

        // mockito
        Authentication auth = Mockito.mock(Authentication.class);
        Mockito.when(authenticationManager.authenticate(any()))
                .thenReturn(auth);
        Mockito.when(session.getAttribute(any(String.class)))
                .thenReturn(null);

        // when
        securityService.loginByUsernamePassword(loginRequest, session);

        // then
        SecurityContext contextFromHolder = SecurityContextHolder.getContext();
        assertNotNull(contextFromHolder);
        assertNotNull(contextFromHolder.getAuthentication());
        assertEquals(auth, contextFromHolder.getAuthentication());
    }

    @Test
    void loginByUsernamePassword_whenLoginDataIsNotCorrect_thenThrowException() {
        // given
        LoginRequest loginRequest = new LoginRequest(null, null);

        // mockito
        Mockito.when(authenticationManager.authenticate(any(Authentication.class)))
                .thenThrow(new AuthenticationException("authentication fail") {});

        // when
        assertThrows(AccountAuthenticationException.class, () -> securityService.loginByUsernamePassword(loginRequest, session));
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void loginByUsernamePassword_whenParameterLoginRequestIsNull_thenThrowException() {
        // given
        LoginRequest loginRequest = null;
        HttpSession httpSession = session;

        // when
        assertThrows(IllegalArgumentException.class, () -> securityService.loginByUsernamePassword(loginRequest, httpSession));
    }

    @Test
    void loginByUsernamePassword_whenParameterSessionIsNull_thenThrowException() {
        // given
        String username = "johndoe123";
        String password = "password123";
        LoginRequest loginRequest = new LoginRequest(username, password);
        HttpSession httpSession = null;

        // when
        assertThrows(IllegalArgumentException.class, () -> securityService.loginByUsernamePassword(loginRequest, httpSession));
    }


/*
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    register(Account account, AccountService accountService)
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
*/
    @Test
    void register_whenItIsOk_thenReturnIdOfRegisteredAccount() {
        // given
        Long expectedId = 10L;
        Account account = new Account();
        AccountService accountService = Mockito.mock(AccountService.class);

        // mockito
        Mockito.when(accountService.createAccount(account))
                .thenReturn(expectedId);

        // when
        Long result = securityService.register(account, accountService);

        // then
        assertNotNull(result);
        assertEquals(expectedId, result);
    }

    @Test
    void register_whenParameterAccountIsNull_thenThrowException() {
        // given
        Account account = null;
        AccountService accountService = Mockito.mock(AccountService.class);

        // when
        assertThrows(IllegalArgumentException.class, () -> securityService.register(account, accountService));
    }

    @Test
    void register_whenParameterAccountServiceIsNull_thenThrowException() {
        // given
        Account account = new Account();
        AccountService accountService = null;

        // when
        assertThrows(IllegalArgumentException.class, () -> securityService.register(account, accountService));
    }


/*
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    reauthenticate(Account account, HttpSession session)
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
*/
    @Test
    void reauthenticate_whenItIsOk_thenReauthenticate() {
        // given
        Account account = AccountGenerator.oneAccount();

        // when
        securityService.reauthenticate(account, session);

        // then
        SecurityContext context = SecurityContextHolder.getContext();
        assertNotNull(context);
        Authentication auth = context.getAuthentication();
        assertNotNull(auth);
        assertEquals(account.getCredential().getLogin(), auth.getName());
        assertNotEquals(account.getCredential().getPassword(), auth.getPrincipal());
        assertFalse(account.getRoles().isEmpty());
    }

    @Test
    void reauthenticate_whenAccountDoesntHaveRoles_thenReauthenticateWithoutRoles() {
        // given
        Account account = AccountGenerator.oneAccount();
        account.getRoles().clear();

        // when
        securityService.reauthenticate(account, session);

        // then
        SecurityContext context = SecurityContextHolder.getContext();
        assertNotNull(context);
        Authentication auth = context.getAuthentication();
        assertNotNull(auth);
        assertEquals(account.getCredential().getLogin(), auth.getName());
        assertNotEquals(account.getCredential().getPassword(), auth.getPrincipal());
        assertTrue(account.getRoles().isEmpty());
    }

    @Test
    void reauthenticate_whenParameterAccountIsNull_thenThrowException() {
        // given
        Account account = null;
        HttpSession sessionParam = session;

        // when
        assertThrows(IllegalArgumentException.class, () -> securityService.reauthenticate(account, sessionParam));
    }

    @Test
    void reauthenticate_whenParameterHppSessionIsNull_thenThrowException() {
        // given
        Account account = new Account();
        HttpSession sessionParam = null;

        // when
        assertThrows(IllegalArgumentException.class, () -> securityService.reauthenticate(account, sessionParam));
    }


/*
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    deauthenticate(Account account, HttpSession session)
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
*/
    @Test
    void deauthenticate_whenAuthenticated_thenDeauthenticate() {
        // given
        var principal = "login123";
        var credentials = "password123";
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, credentials);
        SecurityContext context = getSecurityContext(auth);

        // mockito
        Mockito.when(session.getAttribute(any(String.class)))
                .thenReturn(context);

        // when
        securityService.deauthenticate(session);

        // then
        SecurityContext securityContext = (SecurityContext) session.getAttribute(SPRING_SECURITY_CONTEXT_KEY);
        assertNull(securityContext.getAuthentication());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    SecurityContext getSecurityContext(Authentication auth) {
        return new SecurityContextImpl(auth);
    }

    @Test
    void deauthenticate_whenParameterSessionIsNull_thenThrowException() {
        // given
        HttpSession httpSession = null;

        // when
        assertThrows(IllegalArgumentException.class, () -> securityService.deauthenticate(httpSession));
    }

}
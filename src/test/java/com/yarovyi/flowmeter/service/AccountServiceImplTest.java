package com.yarovyi.flowmeter.service;

import com.yarovyi.flowmeter.dto.auth.PasswordChangeRequest;
import com.yarovyi.flowmeter.entity.account.Account;
import com.yarovyi.flowmeter.entity.account.Credential;
import com.yarovyi.flowmeter.entity.account.PersonalInfo;
import com.yarovyi.flowmeter.entity.account.Role;
import com.yarovyi.flowmeter.repository.AccountRepository;
import com.yarovyi.flowmeter.service.testUtility.entityGenerator.AccountGenerator;
import com.yarovyi.flowmeter.service.testUtility.entityGenerator.CredentialGenerator;
import com.yarovyi.flowmeter.service.testUtility.entityGenerator.PersonalInfoGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {
    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private Role defaultRole = new Role(1L,"ROLE_USER");

    private AccountService service;


    @BeforeEach
    void setUp() {
        service = new AccountServiceImpl(accountRepository, passwordEncoder, defaultRole);
    }


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  getAccounts()
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Test
    void getAccounts_whenAccountsExist_thenReturnAll() {
        // given
        List<Account> expected = AccountGenerator.accounts(10);

        // mockito
        Mockito.when(accountRepository.findAll())
                .thenReturn(expected);

        // when
        List<Account> result = service.getAccounts();

        // then
        assertNotNull(result);
        assertIterableEquals(expected, result);
    }


    @Test
    void getAccounts_whenAccountsNotExist_thenReturnEmptyList() {
        // given
        List<Account> expected = Collections.emptyList();

        // mockito
        Mockito.when(accountRepository.findAll())
                .thenReturn(expected);

        // when
        List<Account> result = service.getAccounts();

        // then
        assertNotNull(result);
        assertIterableEquals(expected, result);
    }


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  getAccountById(Long accountId)
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Test
    void getAccountById_whenAccountExists_thenReturnOptionalWithAccount() {
        // given
        var expected = Optional.of(AccountGenerator.oneAccount());
        Long id = expected.get().getId();

        // mockito
        Mockito.when(accountRepository.findById(id))
                .thenReturn(expected);

        // when
        Optional<Account> result = service.getAccountById(id);

        // then
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(expected.get(), result.get());
    }


    @Test
    void getAccountById_whenAccountNotExists_thenReturnEmptyOptional() {
        // given
        Long id = 10L;
        Optional<Account> expected = Optional.empty();

        // mockito
        Mockito.when(accountRepository.findById(id))
                .thenReturn(expected);

        // when
        Optional<Account> result = service.getAccountById(id);

        // then
        assertNotNull(result);
        assertFalse(result.isPresent());
    }


    @Test
    void getAccountById_whenParameterIsNull_thenThrowException() {
        // given
        Long id = null;

        // when
        assertThrows(IllegalArgumentException.class, () -> service.getAccountById(id));
    }


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  getAccountByLogin(String login)
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Test
    void getAccountByLogin_whenAccountExists_thenReturnOptionalWithAccount() {
        // given
        Optional<Account> expected = Optional.of(AccountGenerator.oneAccount());
        String login = expected.get().getCredential().getLogin();

        // mockito
        Mockito.when(accountRepository.findAccountByCredentialLogin(login))
                .thenReturn(expected);

        // when
        Optional<Account> result = service.getAccountByLogin(login);

        // then
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(expected.get(), result.get());
    }


    @Test
    void getAccountByLogin_whenAccountNotExists_thenReturnEmptyOptional() {
        // given
        String login = "login123";
        Optional<Account> expected = Optional.empty();

        // mockito
        Mockito.when(accountRepository.findAccountByCredentialLogin(login))
                .thenReturn(expected);

        // when
        Optional<Account> result = service.getAccountByLogin(login);

        // then
        assertNotNull(result);
        assertFalse(result.isPresent());
    }


    @Test
    void getAccountByLogin_whenParameterIsNull_thenThrowException() {
        // given
        String login = null;

        // when
        assertThrows(IllegalArgumentException.class, () -> service.getAccountByLogin(login));
    }


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  getAccountByEmail(String email)
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Test
    void getAccountByEmail_whenAccountExists_thenReturnOptionalWithAccount() {
        // given
        Optional<Account> expected = Optional.of(AccountGenerator.oneAccount());
        String email = expected.get().getCredential().getEmail();

        // mockito
        Mockito.when(accountRepository.findAccountByCredentialEmail(email))
                .thenReturn(expected);

        // when
        Optional<Account> result = service.getAccountByEmail(email);

        // then
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(expected.get(), result.get());
    }


    @Test
    void getAccountByEmail_whenAccountNotExists_thenReturnEmptyOptional() {
        // given
        Optional<Account> expected = Optional.empty();
        String email = "some.email@gmail.com";

        // mockito
        Mockito.when(accountRepository.findAccountByCredentialEmail(email))
                .thenReturn(expected);

        // when
        Optional<Account> result = service.getAccountByEmail(email);

        // then
        assertNotNull(result);
        assertFalse(result.isPresent());
    }


    @Test
    void getAccountByEmail_whenParameterIsNull_thenThrowException() {
        // given
        String email = null;

        // when
        assertThrows(IllegalArgumentException.class, () -> service.getAccountByEmail(email));
    }


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  createAccount(Account account)
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Test
    void createAccount_whenAccountIsOk_thenCreateAccountAndReturnAccountId() {
        // given
        Account input = AccountGenerator.oneAccount();
        input.setId(null);

        Account expected = new Account(input);
        expected.setId(10L);

        // mockito
        Mockito.when(accountRepository.save(any()))
                .thenReturn(expected);

        // when
        Long resultId = service.createAccount(input);

        // then
        assertNotNull(resultId);
        assertEquals(expected.getId(), resultId);
    }


    @Test
    void createAccount_whenParameterIsNull_thenThrowException() {
        // given
        Account input = null;

        // when
        assertThrows(IllegalArgumentException.class, () -> service.createAccount(input));
    }


    @Test
    void createAccount_whenAccountIdIsNotNull_thenThrowException() {
        // given
        Account account = AccountGenerator.oneAccount();

        // when
        assertThrows(IllegalArgumentException.class, () -> service.createAccount(account));
    }


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  createAndGetAccount(Account account)
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Test
    void createAndGetAccount_whenAccountIsOk_thenReturnSavedAccount() {
        // given
        Account input = AccountGenerator.oneAccount();
        input.setId(null);

        Account expected = new Account(input);
        expected.setId(10L);

        // mockito
        Mockito.when(accountRepository.save(any(Account.class)))
                .thenReturn(expected);

        // when
        Account result = service.createAndGetAccount(input);

        // then
        assertNotNull(result);
        assertEquals(expected.getId(), result.getId());
    }


    @Test
    void createAndGetAccount_whenAccountIsOk_thenEncodePasswordAndAddDefaultRole() {
        // given
        String password = "password";
        String encodedPassword = "$2a$12$yptXHLvoAXoLkHrQKLS5Je0afeGVRDResoTVPoK6fIb4Vxsg920XC";

        Account input = AccountGenerator.oneAccount();
        input.setId(null);
        input.getCredential().setPassword(password);

        // mockito
        Mockito.when(passwordEncoder.encode(password))
                .thenReturn(encodedPassword);
        Mockito.when(accountRepository.save(any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        service.createAndGetAccount(input);

        // captor
        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        Mockito.verify(accountRepository).save(captor.capture());
        Account beforeSaving = captor.getValue();

        // then
        assertNotNull(beforeSaving);
        assertNotNull(beforeSaving.getCredential());
        assertNotNull(beforeSaving.getRoles());
        assertEquals(encodedPassword, beforeSaving.getCredential().getPassword());
        assertTrue(beforeSaving.getRoles().contains(defaultRole));
    }


    @Test
    void createAndGetAccount_whenParameterIsNull_thenThrowException() {
        // given
        Account input = null;

        // when
        assertThrows(IllegalArgumentException.class, () -> service.createAndGetAccount(input));
    }


    @Test
    void createAndGetAccount_whenIdInAccountIsPresent_throwException() {
        // given
        Account input = AccountGenerator.oneAccount();

        // when
        assertThrows(IllegalArgumentException.class, () -> service.createAndGetAccount(input));
    }


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  updatePersonalInfo(Account account, PersonalInfo personalInfo)
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Test
    void updatePersonalInfo_whenPersonalInfoModified_thenUpdate() {
        // given
        Account account = AccountGenerator.oneAccount();
        PersonalInfo changes = PersonalInfoGenerator.onePersonalInfo();

        // mockito
        Mockito.when(accountRepository.save(any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        service.updatePersonalInfo(account, changes);

        // captor
        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        Mockito.verify(accountRepository).save(captor.capture());
        Account updated = captor.getValue();

        // then
        assertNotNull(updated);
        assertEquals(changes, updated.getPersonalInfo());
    }


    @Test
    void updatePersonalInfo_whenParameterAccountIsNull_throwException() {
        // given
        Account account = null;
        PersonalInfo changes = PersonalInfoGenerator.onePersonalInfo();

        // when
        assertThrows(IllegalArgumentException.class, () -> service.updatePersonalInfo(account,changes));
    }


    @Test
    void updatePersonalInfo_whenParameterPersonalInfoIsNull_throwException() {
        // given
        Account account = AccountGenerator.oneAccount();
        PersonalInfo changes = null;

        // when
        assertThrows(IllegalArgumentException.class, () -> service.updatePersonalInfo(account,changes));
    }


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  updateCredentials(Account account, Credential credential)
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Test
    void updateCredentials_whenCredentialModified_thenReturnUpdatedAccount() {
        // given
        Account account = AccountGenerator.oneAccount();
        Credential changes = CredentialGenerator.oneCredential();

        // mockito
        Mockito.when(accountRepository.save(any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        Account result = service.updateCredentials(account, changes);

        // then
        assertNotNull(result);
        assertNotNull(result.getCredential());
        assertEquals(changes.getLogin(), result.getCredential().getLogin());
        assertEquals(changes.getEmail(), result.getCredential().getEmail());
        assertNotEquals(changes.getPassword(), result.getCredential().getPassword());   // password not updated
    }

    @Test
    void updateCredentials_whenCredentialsModified_thenUpdateAccount() {
        // given
        Account account = AccountGenerator.oneAccount();
        Credential credential = CredentialGenerator.oneCredential();

        // mockito
        Mockito.when(accountRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        service.updateCredentials(account, credential);

        // captor
        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        Mockito.verify(accountRepository).save(captor.capture());
        Account updated = captor.getValue();

        // then
        assertNotNull(updated);
        assertNotNull(updated.getCredential());
        assertEquals(credential.getLogin(), updated.getCredential().getLogin());
        assertEquals(credential.getEmail(), updated.getCredential().getEmail());
        assertNotEquals(credential.getPassword(), updated.getCredential().getPassword());
    }


    @Test
    void updateCredentials_whenCredentialInAccountIsNull_thenThrowException() {
        // given
        Account account = AccountGenerator.oneAccount();
        account.setCredential(null);
        Credential changes = CredentialGenerator.oneCredential();

        // when
        assertThrows(IllegalArgumentException.class, () -> service.updateCredentials(account, changes));
    }


    @Test
    void updateCredentials_whenParameterAccountIsNull_thenThrowException() {
        // given
        Account account = null;
        Credential changes = CredentialGenerator.oneCredential();

        // when
        assertThrows(IllegalArgumentException.class, () -> service.updateCredentials(account, changes));
    }


    @Test
    void updateCredentials_whenParameterCredentialIsNull_thenThrowException() {
        // given
        Account account = AccountGenerator.oneAccount();
        Credential changes = null;

        // when
        assertThrows(IllegalArgumentException.class, () -> service.updateCredentials(account, changes));
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  changePassword(Account account, PasswordChangeRequest passwordChangeRequest)
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Test
    void changePassword_whenNewPasswordIsOk_changePassword() {
        // given
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        String currentPassword = "password123";
        String newPassword = "password456";
        String newPasswordEncoded = "$2a$10$lkHepRxT..CgsDVM0a/Vku9LTvyxPc8R40esu3cnYnqIFdnulIRfC";
        Account account = AccountGenerator.oneAccount(currentPassword);
        PasswordChangeRequest pcRequest = new PasswordChangeRequest(currentPassword, newPassword);

        // todo: replace mocked passwordEncoder with new BCryptPasswordEncoder

        // mockito
        Mockito.when(passwordEncoder.matches(currentPassword, account.getCredential().getPassword()))
                .thenReturn(true);
        Mockito.when(passwordEncoder.matches(newPassword, account.getCredential().getPassword()))
                .thenReturn(false);
        Mockito.when(passwordEncoder.encode(newPassword))
                .thenReturn(newPasswordEncoded);
        Mockito.when(accountRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        service.changePassword(account, pcRequest);
        Mockito.verify(passwordEncoder, times(2)).matches(any(), any());

        // captor
        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        Mockito.verify(accountRepository).save(captor.capture());
        Account updated = captor.getValue();

        // then
        assertNotNull(updated);
        assertNotNull(updated.getCredential());
        assertTrue(encoder.matches(newPassword, updated.getCredential().getPassword()));
    }
}















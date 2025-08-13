package com.yarovyi.flowmeter.service;

import com.yarovyi.flowmeter.dto.auth.PasswordChangeRequest;
import com.yarovyi.flowmeter.entity.account.Account;
import com.yarovyi.flowmeter.entity.account.Credential;
import com.yarovyi.flowmeter.entity.account.PersonalInfo;
import com.yarovyi.flowmeter.entity.account.Role;
import com.yarovyi.flowmeter.exception.AccountAuthenticationException;
import com.yarovyi.flowmeter.exception.SubentityNotFoundException;
import com.yarovyi.flowmeter.repository.AccountRepository;
import com.yarovyi.flowmeter.testUtility.entityGenerator.AccountGenerator;
import com.yarovyi.flowmeter.testUtility.entityGenerator.CredentialGenerator;
import com.yarovyi.flowmeter.testUtility.entityGenerator.PersonalInfoGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {
    @Mock
    private AccountRepository accountRepository;
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private Role defaultRole = new Role(1L,"ROLE_USER");
    private AccountService service;

    @BeforeEach
    void setUp() {
        service = new AccountServiceImpl(accountRepository, passwordEncoder, defaultRole);
    }


/*
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    getAccounts()
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
*/
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


/*
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    getAccountById(Long accountId)
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
*/
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


/*
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    getAccountByLogin(String login)
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
*/
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


/*
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    getAccountByEmail(String email)
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
*/
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


/*
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    createAccount(Account account)
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
*/
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


/*
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    createAndGetAccount(Account account)
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
*/
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
        Long expectedId = 10L;
        String password = "password";
        Account input = AccountGenerator.oneAccount();
        input.setId(null);
        input.getCredential().setPassword(password);

        // mockito
        Mockito.when(accountRepository.save(any(Account.class)))
                .thenAnswer(invocation -> {
                    Account account = invocation.getArgument(0);
                    account.setId(expectedId);

                    return account;
                });

        // when
        Account result = service.createAndGetAccount(input);

        // then
        assertNotNull(result);
        assertNotNull(result.getCredential());
        assertNotNull(result.getRoles());
        assertTrue(result.getRoles().contains(defaultRole));
        assertTrue(passwordEncoder.matches(password, result.getCredential().getPassword()));
        assertEquals(expectedId, result.getId());
    }

    @Test
    void createAndGetAccount_whenParameterAccountIsNull_thenThrowException() {
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


/*
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    updatePersonalInfo(Account account, PersonalInfo personalInfo)
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
*/
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


/*
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    updateCredentials(Account account, Credential credential)
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
*/
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
        Credential changes = CredentialGenerator.oneCredential();

        // mockito
        Mockito.when(accountRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        service.updateCredentials(account, changes);

        // captor
        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        Mockito.verify(accountRepository).save(captor.capture());
        Account updated = captor.getValue();

        // then
        assertNotNull(updated);
        assertNotNull(updated.getCredential());
        assertEquals(changes.getLogin(), updated.getCredential().getLogin());
        assertEquals(changes.getEmail(), updated.getCredential().getEmail());
        assertNotEquals(changes.getPassword(), updated.getCredential().getPassword());
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


/*
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    changePassword(Account account, PasswordChangeRequest passwordChangeRequest)
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
*/
    @Test
    void changePassword_whenNewPasswordIsOk_changePassword() {
        // given
        String currentPassword = "currentPassword123";
        String newPassword = "newSecurePassword123";
        String encodedPassword = passwordEncoder.encode(currentPassword);
        PasswordChangeRequest pcRequest = new PasswordChangeRequest(currentPassword, newPassword);

        Account account = AccountGenerator.oneAccount(currentPassword);
        account.getCredential().setPassword(encodedPassword);

        // mockito
        Mockito.when(accountRepository.save(any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        service.changePassword(account, pcRequest);

        // captor
        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        Mockito.verify(accountRepository).save(captor.capture());
        Account updated = captor.getValue();

        // then
        assertNotNull(updated);
        assertNotNull(updated.getCredential());
        assertTrue(passwordEncoder.matches(newPassword, updated.getCredential().getPassword()));
    }

    @Test
    void changePassword_whenCurrentPasswordsDoNotMatch_thenThrowException() {
        // given
        String currentPassword = "absoluteRandomPassword123";
        String newPassword = "somePassword";

        var pcr = new PasswordChangeRequest(currentPassword, newPassword);
        Account account = AccountGenerator.oneAccount();

        // when
        assertThrows(AccountAuthenticationException.class, () -> service.changePassword(account, pcr));
    }

    @Test
    void changePassword_whenNewPasswordMatchesWithCurrent_thenThrowException() {
        // given
        String current = "password123";
        String newPassword = "password123";

        var pcr = new PasswordChangeRequest(current, newPassword);
        Account account = AccountGenerator.oneAccount();
        account.getCredential().setPassword(passwordEncoder.encode(current));

        // when
        assertThrows(AccountAuthenticationException.class, () -> service.changePassword(account, pcr));
    }

    @Test
    void changePassword_whenParameterAccountIsNull_thenThrowException() {
        // given
        Account account = null;
        var passwordChangeRequest = new PasswordChangeRequest("", "");

        // when
        assertThrows(IllegalArgumentException.class, () -> service.changePassword(account, passwordChangeRequest));
    }

    @Test
    void changePassword_whenParameterPasswordChangeRequestIsNull_thenThrowException() {
        // given
        Account account = AccountGenerator.oneAccount();
        PasswordChangeRequest passwordChangeRequest = null;

        // when
        assertThrows(IllegalArgumentException.class, () -> service.changePassword(account, passwordChangeRequest));
    }


/*
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    deleteAccountById(Long id)
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
*/
    @Test
    void deleteAccountById_whenAccountIsExists_thenDeleteAccount() {
        // then
        long id = 10;
        Account forDelete = AccountGenerator.oneAccount();
        forDelete.setId(id);
        forDelete.setDeleted(false);

        // mockito
        Mockito.when(accountRepository.findById(id))
                .thenReturn(Optional.of(forDelete));

        // when
        service.deleteAccountById(id);

        // captor
        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        Mockito.verify(accountRepository).save(captor.capture());
        Account beforeSaving = captor.getValue();

        // then
        assertNotNull(beforeSaving);
        assertTrue(beforeSaving.isDeleted());
    }

    @Test
    void deleteAccountBuId_whenAccountNotExistsById_thenThrowException() {
        // given
        long id = 10;

        // mockito
        Mockito.when(accountRepository.findById(id))
                .thenReturn(Optional.empty());

        // when
        assertThrows(SubentityNotFoundException.class, () -> service.deleteAccountById(id));
    }

    @Test
    void deleteAccountById_whenParameterIdIsNull_thenThrowException() {
        // given
        Long id = null;

        // when
        assertThrows(IllegalArgumentException.class, () -> service.deleteAccountById(id));
    }


/*
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    existAccountByLogin(String login)
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
*/
    @Test
    void existAccountByLogin_whenAccountIsExists_thenReturnTrue() {
        // given
        String login = "SummerBoy777";

        // mockito
        Mockito.when(accountRepository.existsAccountByCredential_Login(login))
                .thenReturn(true);

        // when
        boolean result = service.existAccountByLogin(login);

        // then
        assertTrue(result);
    }

    @Test
    void existAccountByLogin_whenAccountIsNotExists_thenReturnFalse() {
        // given
        String login = "SummerBoy777";

        // mockito
        Mockito.when(accountRepository.existsAccountByCredential_Login(login))
                .thenReturn(false);

        // when
        boolean result = service.existAccountByLogin(login);

        // then
        assertFalse(result);
    }

    @Test
    void existAccountByLogin_whenParameterLoginIsNull_thenThrowException() {
        // given
        String login = null;

        // when
        assertThrows(IllegalArgumentException.class, () -> service.existAccountByLogin(login));
    }


/*
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    checkOwnership(Long ownerId, Long targetId)
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
*/
    @Test
    void checkOwnership_whenIdsIsMatches_thenReturnTrue() {
        // given
        Long ownerId = 10L;
        Long targetId = 10L;

        // when
        boolean result = service.checkOwnership(ownerId, targetId);

        // then
        assertTrue(result);
    }

    @Test
    void checkOwnership_whenIdsIsNotMatches_thenReturnFalse() {
        // given
        Long ownerId = 10L;
        Long targetId = 15L;

        // when
        boolean result = service.checkOwnership(ownerId, targetId);

        // then
        assertFalse(result);
    }

    @Test
    void checkOwnership_whenParameterOwnerIdIsNull_thenThrowException() {
        // given
        Long ownerId = null;
        Long targetId = 10L;

        // when
        assertThrows(IllegalArgumentException.class, () -> service.checkOwnership(ownerId, targetId));
    }

    @Test
    void checkOwnership_whenParameterTargetIdIsNull_thenThrowException() {
        // given
        Long ownerId = 10L;
        Long targetId = null;

        // when
        assertThrows(IllegalArgumentException.class, () -> service.checkOwnership(ownerId, targetId));
    }

/*
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    checkOwnershipOrElseThrow(Long ownerId, Long targetId)
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
*/
    @Test
    void checkOwnershipOrElseThrow_whenIdsIsMatches_thenDoNothing() {
        // given
        Long ownerId = 10L;
        Long targetId = 10L;

        // when
        service.checkOwnershipOrElseThrow(ownerId, targetId);
    }

    @Test
    void checkOwnershipOrElseThrow_whenIdsIsNotMatches_thenThrowException() {
        // given
        Long ownerId = 10L;
        Long targetId = 15L;

        // when
        assertThrows(SubentityNotFoundException.class, () -> service.checkOwnershipOrElseThrow(ownerId, targetId));
    }

    @Test
    void checkOwnershipOrElseThrow_whenParameterOwnerIdIsNull_thenThrowException() {
        // given
        Long ownerId = null;
        Long targetId = 10L;

        // when
        assertThrows(IllegalArgumentException.class, () -> service.checkOwnershipOrElseThrow(ownerId, targetId));
    }

    @Test
    void checkOwnershipOrElseThrow_whenParameterTargetIdIsNull_thenThrowException() {
        // given
        Long ownerId = 10L;
        Long targetId = null;

        // when
        assertThrows(IllegalArgumentException.class, () -> service.checkOwnershipOrElseThrow(ownerId, targetId));
    }

}















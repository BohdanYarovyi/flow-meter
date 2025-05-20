package com.yarovyi.flowmeter.mapper;

import com.yarovyi.flowmeter.dto.account.AccountDto;
import com.yarovyi.flowmeter.dto.account.AccountCreatedDto;
import com.yarovyi.flowmeter.entity.account.Account;
import com.yarovyi.flowmeter.entity.account.Credential;
import com.yarovyi.flowmeter.entity.account.PersonalInfo;
import com.yarovyi.flowmeter.entity.account.Role;
import com.yarovyi.flowmeter.dto.account.CredentialsDto;
import com.yarovyi.flowmeter.dto.account.PersonalInfoDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

public class AccountMapper {

    public static final Function<Account, AccountDto> ACCOUNT_TO_DTO = (account) -> {
        var credentials = account.getCredential();
        var personalInfo = account.getPersonalInfo();

        return new AccountDto(
                account.getId(),
                account.getCreatedAt(),
                account.getUpdatedAt(),
                account.getRoles(),
                credentials.getLogin(),
                credentials.getEmail(),
                personalInfo.getFirstname(),
                personalInfo.getLastname(),
                personalInfo.getPatronymic(),
                personalInfo.getDateOfBirth(),
                personalInfo.getPhone()
        );
    };

    public static final Function<List<Account>, List<AccountDto>> ACCOUNTs_TO_DTOs = (accounts) -> {
        return accounts.stream()
                .map(ACCOUNT_TO_DTO)
                .toList();
    };

    public static final Function<AccountCreatedDto, Account> ACCOUNT_CREATED_DTO_TO_ACCOUNT = (dto) -> {
        var account = new Account();

        var credentials = new Credential();
        credentials.setLogin(dto.login());
        credentials.setEmail(dto.email());
        credentials.setPassword(dto.password());

        var info = new PersonalInfo();
        info.setFirstname(dto.firstname());
        info.setLastname(dto.lastname());
        info.setPatronymic(dto.patronymic());
        info.setPhone(dto.phone());
        info.setDateOfBirth(dto.dateOfBirth());

        var roles = new ArrayList<Role>();

        account.setCredential(credentials);
        account.setPersonalInfo(info);
        account.setRoles(roles);

        return account;
    };

    public static final BiFunction<PersonalInfo, Account, Account> COMMIT_PERSONAL_INFO_UPDATES = (updates, account) -> {
        if (Objects.isNull(account.getPersonalInfo())) {
            account.setPersonalInfo(new PersonalInfo());
        }

        PersonalInfo personalInfo = account.getPersonalInfo();
        if (!Objects.equals(personalInfo.getFirstname(), updates.getFirstname())) {
            personalInfo.setFirstname(updates.getFirstname());
        }

        if (!Objects.equals(personalInfo.getLastname(), updates.getLastname())) {
            personalInfo.setLastname(updates.getLastname());
        }

        if (!Objects.equals(personalInfo.getPatronymic(), updates.getPatronymic())) {
            personalInfo.setPatronymic(updates.getPatronymic());
        }

        if (!Objects.equals(personalInfo.getDateOfBirth(), updates.getDateOfBirth())) {
            personalInfo.setDateOfBirth(updates.getDateOfBirth());
        }

        if (!Objects.equals(personalInfo.getPhone(), updates.getPhone())) {
            personalInfo.setPhone(updates.getPhone());
        }

        return account;
    };

    // It is impossible, that the account don't have credentials
    public static final BiFunction<Credential, Account, Account> COMMIT_CREDENTIALS_UPDATES = (credentials, account) -> {
        var existCredentials = account.getCredential();

        if (!Objects.equals(existCredentials.getLogin(), credentials.getLogin())) {
            existCredentials.setLogin(credentials.getLogin());
        }

        if (!Objects.equals(existCredentials.getEmail(), credentials.getEmail())) {
            existCredentials.setEmail(credentials.getEmail());
        }

        return account;
    };

    public static final Function<PersonalInfoDto, PersonalInfo> DTO_TO_PERSONAL_INFO = (dto) -> {
        return new PersonalInfo(
                dto.firstname(),
                dto.lastname(),
                dto.patronymic(),
                dto.dateOfBirth(),
                dto.phone()
        );
    };

    public static final Function<CredentialsDto, Credential> DTO_TO_CREDENTIALS = (dto) -> {
        return new Credential(
                dto.login(),
                dto.email(),
                null
        );
    };

}











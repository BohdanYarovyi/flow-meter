package com.yarovyi.flowmeter.util;

import com.yarovyi.flowmeter.entity.dto.AccountDto;
import com.yarovyi.flowmeter.entity.dto.AccountCreatedDto;
import com.yarovyi.flowmeter.domain.account.Account;
import com.yarovyi.flowmeter.domain.account.Credentials;
import com.yarovyi.flowmeter.domain.account.PersonalInfo;
import com.yarovyi.flowmeter.domain.account.Role;
import com.yarovyi.flowmeter.entity.dto.CredentialsDto;
import com.yarovyi.flowmeter.entity.dto.PersonalInfoDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

public class AccountMapper {

    public static final Function<Account, AccountDto> ACCOUNT_TO_DTO = (account) -> {
        var credentials = account.getCredentials();
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

    public static final Function<AccountCreatedDto,Account> ACCOUNT_CREATED_DTO_TO_ACCOUNT = (dto) -> {
        var account = new Account();

        var credentials = new Credentials();
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

        account.setCredentials(credentials);
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
    public static final BiFunction<Credentials, Account, Account> COMMIT_CREDENTIALS_UPDATES = (credentials, account) -> {
        var existCredentials = account.getCredentials();

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

    public static final Function<CredentialsDto, Credentials> DTO_TO_CREDENTIALS = (dto) -> {
        return new Credentials(
                dto.login(),
                dto.email(),
                null
        );
    };

}











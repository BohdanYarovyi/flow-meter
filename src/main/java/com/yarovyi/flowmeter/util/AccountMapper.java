package com.yarovyi.flowmeter.util;

import com.yarovyi.flowmeter.entity.dto.AccountDto;
import com.yarovyi.flowmeter.entity.dto.AccountCreatedDto;
import com.yarovyi.flowmeter.domain.account.Account;
import com.yarovyi.flowmeter.domain.account.Credentials;
import com.yarovyi.flowmeter.domain.account.PersonalInfo;
import com.yarovyi.flowmeter.domain.account.Role;
import com.yarovyi.flowmeter.entity.dto.UpdatedPersonalInfoDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

public class AccountMapper {

    public static final Function<Account, AccountDto> ACCOUNT_TO_DTO = (account) -> {
        var credentials = account.getCredentials();
        var personalInfo = account.getPersonalInfo();

        AccountDto dto = new AccountDto(
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

        return dto;
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

    public static final BiFunction<UpdatedPersonalInfoDto, Account, Account> COMMIT_PERSONAL_INFO_UPDATES = (updates, account) -> {
        if (Objects.isNull(account.getPersonalInfo())) {
            account.setPersonalInfo(new PersonalInfo());
        }

        PersonalInfo personalInfo = account.getPersonalInfo();
        if (!Objects.equals(personalInfo.getFirstname(), updates.firstname())) {
            personalInfo.setFirstname(updates.firstname());
        }

        if (!Objects.equals(personalInfo.getLastname(), updates.lastname())) {
            personalInfo.setLastname(updates.lastname());
        }

        if (!Objects.equals(personalInfo.getPatronymic(), updates.patronymic())) {
            personalInfo.setPatronymic(updates.patronymic());
        }

        if (!Objects.equals(personalInfo.getDateOfBirth(), updates.dateOfBirth())) {
            personalInfo.setDateOfBirth(updates.dateOfBirth());
        }

        if (!Objects.equals(personalInfo.getPhone(), updates.phone())) {
            personalInfo.setPhone(updates.phone());
        }

        return account;
    };

}











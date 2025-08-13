package com.yarovyi.flowmeter.testUtility.entityGenerator;

import com.yarovyi.flowmeter.entity.account.Account;
import org.instancio.Instancio;
import org.instancio.Model;

import java.util.ArrayList;
import java.util.List;

import static com.yarovyi.flowmeter.testUtility.entityGenerator.BaseEntityGenerator.generateWithBaseEntity;
import static com.yarovyi.flowmeter.testUtility.entityGenerator.CredentialGenerator.oneCredential;
import static com.yarovyi.flowmeter.testUtility.entityGenerator.FlowGenerator.flows;
import static com.yarovyi.flowmeter.testUtility.entityGenerator.PersonalInfoGenerator.onePersonalInfo;
import static com.yarovyi.flowmeter.testUtility.entityGenerator.RoleGenerator.roles;
import static org.instancio.Select.field;

public class AccountGenerator implements AccountGeneratorRule {

    public static Account oneAccount() {
        return Instancio.of(base()).create();
    }

    public static List<Account> accounts(int count) {
        return Instancio.ofList(base())
                .size(count)
                .create();
    }

    public static Account oneAccount(String password) {
        var credential = oneCredential(password);

        return Instancio.of(base())
                .supply(field(Account::getCredential), () -> credential)
                .create();
    }

    private static Model<Account> base() {
        var possibleRoles = roles(3);
        var flows = new ArrayList(flows(3, 6, 4));
        var credentials = oneCredential();
        var personalInfo = onePersonalInfo();

        return generateWithBaseEntity(Account.class)
                .supply(field(Account::getRoles), () -> possibleRoles)
                .supply(field(Account::getFlows), () -> flows)
                .supply(field(Account::getCredential), () -> credentials)
                .supply(field(Account::getPersonalInfo), () -> personalInfo)
                .toModel();
    }

}

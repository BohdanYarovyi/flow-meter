package com.yarovyi.flowmeter.testUtility.entityGenerator;

import com.yarovyi.flowmeter.entity.account.PersonalInfo;
import org.instancio.Instancio;
import org.instancio.Model;

import static org.instancio.Select.field;

public class PersonalInfoGenerator implements PersonalInfoGeneratorRule {

    private static Model<PersonalInfo> base() {
        return Instancio.of(PersonalInfo.class)
                .generate(field(PersonalInfo::getFirstname), FIRSTNAME_GENERATOR)
                .generate(field(PersonalInfo::getLastname), LASTNAME_GENERATOR)
                .generate(field(PersonalInfo::getPatronymic), PATRONYMIC_GENERATOR)
                .generate(field(PersonalInfo::getDateOfBirth), DATE_OF_BIRTH_GENERATOR)
                .generate(field(PersonalInfo::getPhone), PHONE_GENERATOR)
                .toModel();
    }

    public static PersonalInfo onePersonalInfo() {
        return Instancio.of(base()).create();
    }

}

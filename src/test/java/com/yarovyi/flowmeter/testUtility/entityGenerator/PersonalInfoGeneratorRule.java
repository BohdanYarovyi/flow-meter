package com.yarovyi.flowmeter.testUtility.entityGenerator;

import org.instancio.GeneratorSpecProvider;

import java.time.LocalDate;

public interface PersonalInfoGeneratorRule {

    GeneratorSpecProvider<String> FIRSTNAME_GENERATOR = gen -> gen
            .string()
            .length(0, 100)
            .mixedCase()
            .alphaNumeric()
            .nullable();

    GeneratorSpecProvider<String> LASTNAME_GENERATOR = gen -> gen
            .string()
            .length(0, 100)
            .mixedCase()
            .alphaNumeric()
            .nullable();

    GeneratorSpecProvider<String> PATRONYMIC_GENERATOR = gen -> gen
            .string()
            .length(0, 100)
            .mixedCase()
            .alphaNumeric()
            .nullable();

    GeneratorSpecProvider<LocalDate> DATE_OF_BIRTH_GENERATOR = gen -> gen
            .temporal()
            .localDate()
            .past()
            .min(LocalDate.of(1950, 1, 1))
            .nullable();

    GeneratorSpecProvider<String> PHONE_GENERATOR = gen -> gen
            .ints()
            .range(100_000_000, 999_999_999)
            .nullable()
            .as(String::valueOf);

}

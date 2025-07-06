package com.yarovyi.flowmeter.service.testUtility.entityGenerator;

import org.instancio.GeneratorSpecProvider;

public interface CredentialGeneratorRule {
    String EMAIL_SUFFIX = "@gmail.com";

    GeneratorSpecProvider<String> LOGIN_GENERATOR = gen -> gen
            .string()
            .length(3, 100)
            .alphaNumeric()
            .mixedCase();

    GeneratorSpecProvider<String> EMAIL_GENERATOR = gen -> gen
            .string()
            .length(11 - EMAIL_SUFFIX.length(), 100 - EMAIL_SUFFIX.length())
            .alphaNumeric()
            .mixedCase()
            .suffix(EMAIL_SUFFIX);

}

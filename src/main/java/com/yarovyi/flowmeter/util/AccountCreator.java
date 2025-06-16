package com.yarovyi.flowmeter.util;

import com.cemiltokatli.passwordgenerate.Password;
import com.cemiltokatli.passwordgenerate.PasswordType;

import java.util.Random;

public class AccountCreator {

    public static String generateNewLogin(String firstname, String lastname, long googleId) {
        String suggestedLoginTemplate = "%s_%s_%s";
        Random random = new Random();
        int randomBound = 200_000;

        long number = (googleId + random.nextInt(randomBound));
        return suggestedLoginTemplate.formatted(firstname, lastname, number);
    }

    public static String generatePassword(int length) {
        Password password = Password.createPassword(PasswordType.ALPHANUMERIC, length);

        return password.generate();
    }

}

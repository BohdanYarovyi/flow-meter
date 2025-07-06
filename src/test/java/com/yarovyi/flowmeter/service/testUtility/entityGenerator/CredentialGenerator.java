package com.yarovyi.flowmeter.service.testUtility.entityGenerator;

import com.yarovyi.flowmeter.entity.account.Credential;
import org.instancio.Instancio;
import org.instancio.Model;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.instancio.Select.field;

public class CredentialGenerator implements CredentialGeneratorRule {
    private static final PasswordEncoder encoder = new BCryptPasswordEncoder();

    private static Model<Credential> base() {
        return Instancio.of(Credential.class)
                .generate(field(Credential::getLogin), LOGIN_GENERATOR)
                .generate(field(Credential::getEmail), EMAIL_GENERATOR)
                .toModel();
    }

    public static Credential oneCredential() {
        String password = "password";

        return Instancio.of(base())
                .supply(field(Credential::getPassword), () -> encoder.encode(password))
                .create();
    }

    public static Credential oneCredential(String password) {
        return Instancio.of(base())
                .supply(field(Credential::getPassword), () -> encoder.encode(password))
                .create();
    }

}

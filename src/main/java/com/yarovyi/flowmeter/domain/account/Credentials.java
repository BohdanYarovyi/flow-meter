package com.yarovyi.flowmeter.domain.account;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable

public class Credentials {
    @Column(name = "c_login")
    private String login;

    @Column(name = "c_email")
    private String email;

    @Column(name = "c_password")
    private String password;
}

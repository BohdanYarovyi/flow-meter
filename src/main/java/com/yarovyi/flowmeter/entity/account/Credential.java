package com.yarovyi.flowmeter.entity.account;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Credential {
    @Column(name = "login")
    private String login;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Credential that = (Credential) object;

        return Objects.equals(login, that.login)
               && Objects.equals(email, that.email)
               && Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(login, email, password);
    }

    @Override
    public String toString() {
        return "Credential{" +
               "login='" + login + '\'' +
               ", email='" + email + '\'' +
               ", password='" + password + '\'' +
               '}';
    }

}

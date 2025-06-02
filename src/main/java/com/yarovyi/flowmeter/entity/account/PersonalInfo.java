package com.yarovyi.flowmeter.entity.account;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class PersonalInfo {
    @Column(name = "firstname")
    private String firstname;

    @Column(name = "lastname")
    private String lastname;

    @Column(name = "patronymic")
    private String patronymic;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "phone")
    private String phone;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        PersonalInfo that = (PersonalInfo) object;

        return Objects.equals(firstname, that.firstname)
               && Objects.equals(lastname, that.lastname)
               && Objects.equals(patronymic, that.patronymic)
               && Objects.equals(dateOfBirth, that.dateOfBirth)
               && Objects.equals(phone, that.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstname, lastname, patronymic, dateOfBirth, phone);
    }

}

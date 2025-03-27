package com.yarovyi.flowmeter.domain.account;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@Embeddable
public class PersonalInfo {
    @Column(name = "c_firstname")
    private String firstname;

    @Column(name = "c_lastname")
    private String lastname;

    @Column(name = "c_patronymic")
    private String patronymic;

    @Column(name = "c_date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "c_phone")
    private String phone;
}

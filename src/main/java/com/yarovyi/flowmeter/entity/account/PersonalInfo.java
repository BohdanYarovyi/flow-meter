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

    /**
     * Constructor for creating independence {@code PersonalInfo}.
     * @apiNote be careful, the constructor copies the object without preserving two-way relationships,
     * as JPA entities do
     * @param other other {@code PersonalInfo} object
     */
    public PersonalInfo(PersonalInfo other) {
        this.firstname = other.firstname;
        this.lastname = other.lastname;
        this.patronymic = other.patronymic;
        this.dateOfBirth = other.dateOfBirth == null ? null : LocalDate.from(other.dateOfBirth);
        this.phone = other.phone;
    }

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

    @Override
    public String toString() {
        return "PersonalInfo{" +
               "firstname='" + firstname + '\'' +
               ", lastname='" + lastname + '\'' +
               ", patronymic='" + patronymic + '\'' +
               ", dateOfBirth=" + dateOfBirth +
               ", phone='" + phone + '\'' +
               '}';
    }

}

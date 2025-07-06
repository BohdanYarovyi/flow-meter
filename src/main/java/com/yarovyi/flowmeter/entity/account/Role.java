package com.yarovyi.flowmeter.entity.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @JsonIgnore
    @ManyToMany(mappedBy = "roles")
    private Set<Account> accounts;

    public Role(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Constructor for creating independence {@code Role}.
     * @apiNote be careful, the constructor copies the object without preserving two-way relationships,
     * as JPA entities do
     * @param other other {@code Role} object
     */
    public Role(Role other) {
        this.id = other.id;
        this.name = other.name;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Role role = (Role) object;

        return Objects.equals(id, role.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Role{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", accounts.size=" + (accounts == null ? null : accounts.size()) +
               '}';
    }
}

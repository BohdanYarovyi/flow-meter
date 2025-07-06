package com.yarovyi.flowmeter.entity.account;

import com.yarovyi.flowmeter.entity.BaseEntity;
import com.yarovyi.flowmeter.entity.flow.Flow;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "account")
@SQLRestriction("deleted = false") // for soft-deleting
public class Account extends BaseEntity {
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "account_role",
            joinColumns = @JoinColumn(name = "account_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    @OneToMany(mappedBy = "account")
    private List<Flow> flows;

    @Embedded
    private Credential credential;

    @Embedded
    private PersonalInfo personalInfo;

    /**
     * Constructor for creating independence {@code Account}.
     * @apiNote be careful, the constructor copies the object without preserving two-way relationships,
     * as JPA entities do
     * @param other other {@code Account} object
     */
    public Account(Account other) {
        super(other);
        copyRoles(other);
        copyFlows(other);
        this.credential = other.credential == null ? null : new Credential(other.credential);
        this.personalInfo = other.personalInfo == null ? null : new PersonalInfo(other.personalInfo);
    }

    private void copyFlows(Account other) {
        if (other.flows != null) {
            this.flows = new ArrayList<>();
            for (Flow flow : other.flows) {
                var tmp = new Flow(flow);
                this.flows.add(tmp);
            }
        }
    }

    private void copyRoles(Account other) {
        if (other.roles != null) {
            this.roles = new HashSet<>();
            for (Role role : other.roles) {
                var tmp = new Role(role);
                this.roles.add(tmp);
            }
        }
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        return super.equals(object);
    }

    @Override
    public String toString() {
        return "Account{" +
               "roles.size=" + roles.size() +
               ", flows.size=" + flows.size() +
               ", credential=" + credential +
               ", personalInfo=" + personalInfo +
               "} " + super.toString();
    }

}


package com.yarovyi.flowmeter.entity.account;

import com.yarovyi.flowmeter.entity.BaseEntity;
import com.yarovyi.flowmeter.entity.flow.Flow;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
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
    private List<Role> roles;   // todo: Set<Role>

    @OneToMany(mappedBy = "account")
    private List<Flow> flows;

    @Embedded
    private Credential credential;

    @Embedded
    private PersonalInfo personalInfo;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        return super.equals(object);
    }

}


package com.yarovyi.flowmeter.domain.account;

import com.yarovyi.flowmeter.domain.BaseEntity;
import com.yarovyi.flowmeter.domain.flow.Flow;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Entity
@Table(name = "t_accounts")
@SQLRestriction("c_deleted = false") // for soft-deleting
public class Account extends BaseEntity {
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "t_accounts_roles",
            joinColumns = @JoinColumn(name = "account_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles;

    @OneToMany(mappedBy = "account")
    private List<Flow> flows;

    @Embedded
    private Credentials credentials;

    @Embedded
    private PersonalInfo personalInfo;


}


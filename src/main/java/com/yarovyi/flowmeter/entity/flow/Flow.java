package com.yarovyi.flowmeter.entity.flow;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yarovyi.flowmeter.entity.BaseEntity;
import com.yarovyi.flowmeter.entity.account.Account;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.SQLRestriction;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "flow")
@SQLRestriction("deleted = false") // for soft-deleting
public class Flow extends BaseEntity {
    @Column(name = "title")
    private String title;
    @Column(name = "description")
    private String description;
    @Column(name = "target_percentage")
    private int targetPercentage;
    @JsonIgnore
    @ManyToOne
    private Account account;
    @OneToMany(mappedBy = "flow")
    private Set<Step> steps;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        return super.equals(object);
    }

}

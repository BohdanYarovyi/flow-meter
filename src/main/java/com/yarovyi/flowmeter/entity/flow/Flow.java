package com.yarovyi.flowmeter.entity.flow;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yarovyi.flowmeter.entity.BaseEntity;
import com.yarovyi.flowmeter.entity.account.Account;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "t_flows")
@SQLRestriction("c_deleted = false") // for soft-deleting
public class Flow extends BaseEntity {
    @Column(name = "c_title")
    private String title;
    @Column(name = "c_description")
    private String description;
    @Column(name = "c_target_percentage")
    private int targetPercentage;
    @JsonIgnore
    @ManyToOne
    private Account account;
    @OneToMany(mappedBy = "flow")
    private List<Step> steps;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        return super.equals(object);
    }

}

package com.yarovyi.flowmeter.domain.flow;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yarovyi.flowmeter.domain.BaseEntity;
import com.yarovyi.flowmeter.domain.account.Account;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
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

}

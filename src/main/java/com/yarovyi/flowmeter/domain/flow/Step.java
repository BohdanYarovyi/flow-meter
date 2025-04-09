package com.yarovyi.flowmeter.domain.flow;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yarovyi.flowmeter.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "t_steps")
@SQLRestriction("c_deleted = false") // for soft-deleting
public class Step extends BaseEntity {
    @Column(name = "c_day")
    private LocalDate day;
    @JsonIgnore
    @ManyToOne
    private Flow flow;
    @OneToMany(mappedBy = "step")
    private List<Case> cases;
}

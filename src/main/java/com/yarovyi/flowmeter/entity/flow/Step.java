package com.yarovyi.flowmeter.entity.flow;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yarovyi.flowmeter.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        return super.equals(object);
    }

}

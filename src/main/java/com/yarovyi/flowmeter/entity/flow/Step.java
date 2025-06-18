package com.yarovyi.flowmeter.entity.flow;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yarovyi.flowmeter.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "step")
@SQLRestriction("deleted = false") // for soft-deleting
public class Step extends BaseEntity {
    @Column(name = "day")
    private LocalDate day;
    @JsonIgnore
    @ManyToOne
    private Flow flow;
    @OneToMany(mappedBy = "step")
    private Set<Case> cases;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        return super.equals(object);
    }

}

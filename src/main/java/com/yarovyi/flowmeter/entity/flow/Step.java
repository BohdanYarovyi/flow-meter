package com.yarovyi.flowmeter.entity.flow;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yarovyi.flowmeter.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.util.HashSet;
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
    @OneToMany(mappedBy = "step", fetch = FetchType.EAGER)
    private Set<Case> cases;

    /**
     * Constructor for creating independence {@code Step}.
     * @apiNote be careful, the constructor copies the object without preserving two-way relationships,
     * as JPA entities do
     * @param other other {@code Step} object
     */
    public Step(Step other) {
        super(other);
        this.day = other.day == null ? null : LocalDate.from(other.day);
        copyCases(other);
    }

    private void copyCases(Step other) {
        if (other.cases != null) {
            this.cases = new HashSet<>();

            for (Case aCase : other.cases) {
                var tmp = new Case(aCase);
                this.cases.add(tmp);
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
        return "Step{" +
               "day=" + day +
               ", flow=[CUT]" +
               ", cases=" + cases +
               "} " + super.toString();
    }
}

package com.yarovyi.flowmeter.entity.flow;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yarovyi.flowmeter.entity.BaseEntity;
import com.yarovyi.flowmeter.entity.account.Account;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.SQLRestriction;

import java.util.HashSet;
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

    /**
     * Constructor for creating independence {@code Flow}.
     * @apiNote be careful, the constructor copies the object without preserving two-way relationships,
     * as JPA entities do
     * @param other other {@code Flow} object
     */
    public Flow(Flow other) {
        super(other);
        this.title = other.title;
        this.description = other.description;
        this.targetPercentage = other.targetPercentage;

        copySteps(other);
    }

    private void copySteps(Flow other) {
        if (other.steps != null) {
            this.steps = new HashSet<>();
            for (Step step : other.steps) {
                var tmp = new Step(step);
                this.steps.add(tmp);
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
        return "Flow{" +
               "title='" + title + '\'' +
               ", description='" + description + '\'' +
               ", targetPercentage=" + targetPercentage +
               ", account=[CUT]" +
               ", steps=" + steps +
               "} " + super.toString();
    }
}

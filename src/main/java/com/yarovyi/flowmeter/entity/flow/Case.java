package com.yarovyi.flowmeter.entity.flow;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yarovyi.flowmeter.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "cases")
@SQLRestriction("deleted = false") // for soft-deleting
public class Case extends BaseEntity {
    @Column(name = "text")
    private String text;
    @Column(name = "percent")
    private int percent;
    @Column(name = "counting")
    private boolean counting;
    @JsonIgnore
    @ManyToOne
    private Step step;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        return super.equals(object);
    }

}

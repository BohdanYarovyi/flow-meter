package com.yarovyi.flowmeter.domain.flow;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yarovyi.flowmeter.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "t_cases")
@SQLRestriction("c_deleted = false") // for soft-deleting
public class Case extends BaseEntity {
    @Column(name = "c_text")
    private String text;
    @Column(name = "c_percent")
    private int percent;
    @Column(name = "c_counting")
    private boolean counting;
    @JsonIgnore
    @ManyToOne
    private Step step;
}

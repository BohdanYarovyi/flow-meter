package com.yarovyi.flowmeter.domain.flow;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yarovyi.flowmeter.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@Entity(name = "t_cases")
public class Case extends BaseEntity {
    @Column(name = "c_text")
    private String text;
    @Column(name = "c_percent")
    private int percent;
    @JsonIgnore
    @ManyToOne
    private Step step;
}

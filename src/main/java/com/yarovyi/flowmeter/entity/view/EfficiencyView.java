package com.yarovyi.flowmeter.entity.view;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "v_statistics_data")
public class EfficiencyView {
    @Id
    @Column(name = "step_id")
    private Long stepId;
    @Column(name = "flow_id")
    private Long flowId;
    @Column(name = "flow_title")
    private String flowTitle;
    @Column(name = "year")
    private Integer year;
    @Column(name = "month")
    private String month;
    @Column(name = "day")
    private Integer day;
    @Column(name = "full_date")
    private LocalDate fullDate;
    @Column(name = "average_percent")
    private Integer averagePercent;
}

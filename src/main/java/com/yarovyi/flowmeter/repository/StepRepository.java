package com.yarovyi.flowmeter.repository;

import com.yarovyi.flowmeter.domain.flow.Step;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface StepRepository extends JpaRepository<Step, Long> {
    boolean existsStepByDayAndFlow_Id(LocalDate day, Long flowId);
}

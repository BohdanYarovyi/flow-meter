package com.yarovyi.flowmeter.repository;

import com.yarovyi.flowmeter.entity.flow.Step;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface StepRepository extends JpaRepository<Step, Long> {
    // here maybe create some constraint in db
    boolean existsStepByDayAndFlow_Id(LocalDate day, Long flowId);
    boolean existsByIdAndFlow_Account_Id(Long stepId, Long accountId);
}

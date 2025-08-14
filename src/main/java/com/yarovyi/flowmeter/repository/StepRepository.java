package com.yarovyi.flowmeter.repository;

import com.yarovyi.flowmeter.entity.flow.Step;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface StepRepository extends JpaRepository<Step, Long> {
    // here maybe create some constraint in db
    boolean existsStepByDayAndFlow_Id(LocalDate day, Long flowId);

    boolean existsByIdAndFlow_Account_Id(Long stepId, Long accountId);

    @Query(value = """
        SELECT DISTINCT s FROM Step s
        LEFT JOIN FETCH s.cases
        WHERE s.id = :stepId
    """)
    Optional<Step> getStepWithEagerFetchById(@Param("stepId") Long stepId);
}

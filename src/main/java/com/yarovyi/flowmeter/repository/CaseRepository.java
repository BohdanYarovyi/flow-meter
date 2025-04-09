package com.yarovyi.flowmeter.repository;

import com.yarovyi.flowmeter.domain.flow.Case;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CaseRepository extends JpaRepository<Case, Long> {
    @Query(value = """
            SELECT EXISTS(
                SELECT 1
                FROM t_cases c
                JOIN t_steps s on c.step_id = s.id
                JOIN t_flows f on s.flow_id = f.id
                WHERE c.id = :caseId
                AND f.account_id = :accountId
            );
            """, nativeQuery = true)
    boolean existsByIdAndAccountId(@Param("caseId") Long caseId, @Param("accountId") Long accountId);
}

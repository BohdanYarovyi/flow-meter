package com.yarovyi.flowmeter.repository;

import com.yarovyi.flowmeter.entity.flow.Case;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CaseRepository extends JpaRepository<Case, Long> {
    @Query(value = """
            SELECT EXISTS(
                SELECT 1
                FROM cases c
                JOIN step s on c.step_id = s.id
                JOIN flow f on s.flow_id = f.id
                WHERE c.id = :caseId
                AND f.account_id = :accountId
            );
            """, nativeQuery = true)
    boolean existsByIdAndAccountId(@Param("caseId") Long caseId, @Param("accountId") Long accountId);
}

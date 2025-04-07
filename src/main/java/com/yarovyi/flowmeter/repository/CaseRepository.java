package com.yarovyi.flowmeter.repository;

import com.yarovyi.flowmeter.domain.flow.Case;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CaseRepository extends JpaRepository<Case, Long> {

    @Query(value = """
            SELECT c FROM Case c
            WHERE c.id = :caseId
            AND c.step.flow.account.id = :accountId
            """
    )
    Optional<Case> findCaseByIdAndAccountId(@Param("caseId") Long caseId, @Param("accountId") Long accountId);

}

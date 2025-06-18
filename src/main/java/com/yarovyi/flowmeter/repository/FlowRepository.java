package com.yarovyi.flowmeter.repository;

import com.yarovyi.flowmeter.entity.flow.Flow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FlowRepository extends JpaRepository<Flow, Long> {

    @Query(value = """
        SELECT f FROM Flow f
        JOIN FETCH f.steps s
        JOIN FETCH s.cases
        WHERE f.account.id = :accountId
    """)
    List<Flow> findAllByAccountIdWithEagerFetch(@Param("accountId") Long accountId);

    boolean existsByIdAndAccount_Id(Long id, Long accountId);
}

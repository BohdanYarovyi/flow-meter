package com.yarovyi.flowmeter.repository;

import com.yarovyi.flowmeter.entity.flow.Flow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FlowRepository extends JpaRepository<Flow, Long> {

    boolean existsByIdAndAccount_Id(Long id, Long accountId);

    @Query(value = """
        SELECT DISTINCT f FROM Flow f
        WHERE f.account.id = :id
    """
    )
    List<Flow> findFlowsByAccount_Id(@Param("id") Long id);

    @Query(value = """
        SELECT DISTINCT f FROM Flow f
        LEFT JOIN FETCH f.steps steps
        WHERE f IN :flows
    """
    )
    List<Flow> fetchStepsToFlows(@Param("flows") List<Flow> flows);

    @Query(value = """
        SELECT DISTINCT f FROM Flow f
        LEFT JOIN FETCH f.steps steps
        LEFT JOIN FETCH steps.cases cases
        WHERE f IN :flows
    """
    )
    List<Flow> fetchCasesToFlowsWithSteps(@Param("flows") List<Flow> flows);


}

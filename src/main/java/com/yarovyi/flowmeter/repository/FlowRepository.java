package com.yarovyi.flowmeter.repository;

import com.yarovyi.flowmeter.domain.flow.Flow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FlowRepository extends JpaRepository<Flow,Long> {
    List<Flow> findAllByAccount_Id(Long accountId);
    boolean existsByIdAndAccount_Id(Long id, Long accountId);
}

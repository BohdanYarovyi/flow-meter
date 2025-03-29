package com.yarovyi.flowmeter.repository;

import com.yarovyi.flowmeter.domain.flow.Case;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CaseRepository extends JpaRepository<Case, Long> {
}

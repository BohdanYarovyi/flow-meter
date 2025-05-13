package com.yarovyi.flowmeter.service;

import com.yarovyi.flowmeter.entity.dto.UniqueMonth;

import java.util.Set;

public interface StatisticsService {

    Set<UniqueMonth> getSortedUniqueMonths(Long flowId);

}

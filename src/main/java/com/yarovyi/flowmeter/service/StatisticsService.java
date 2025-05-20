package com.yarovyi.flowmeter.service;

import com.yarovyi.flowmeter.dto.stat.StatInterval;
import com.yarovyi.flowmeter.dto.stat.UniqueMonth;

import java.util.Set;

public interface StatisticsService {

    Set<UniqueMonth> getSortedUniqueMonths(Long flowId);
    StatInterval getStatisticsForMonth(Long flowId, int year, String month);
    StatInterval getStatisticsForLastWeek(Long flowId);
    StatInterval getStatisticsForLastMonth(Long flowId);
    StatInterval getStatisticsForLastYear(Long flowId);

}

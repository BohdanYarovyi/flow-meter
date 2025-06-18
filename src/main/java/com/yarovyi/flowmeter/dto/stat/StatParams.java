package com.yarovyi.flowmeter.dto.stat;

import com.yarovyi.flowmeter.strategy.statistics.StatisticsScope;

import java.time.LocalDate;

/**
 * StatParams contains parameters for creating statistics.
 * If you use {@link StatisticsScope#MONTH}, you need provide all fields to get statistics for month.
 * If you use {@link StatisticsScope#LAST_WEEK},
 * {@link StatisticsScope#LAST_MONTH} or {@link StatisticsScope#LAST_YEAR}, you need provide only scope and flowId.
 *
 * @param scope  a specific range of statistics
 * @param flowId ID of the flow for which statistics are taken
 * @param year   optional; year, which helps determine a specific month
 * @param month  optional; month for getting statistic
 */
public record StatParams(
        StatisticsScope scope,
        Long flowId,
        Integer year,
        String month
) {

    public Integer getYearDependsScope() {
        if (StatisticsScope.MONTH.equals(scope)) {
            return year;
        }

        return LocalDate.now().getYear();
    }

    public String getIntervalDependsScope() {
        if (StatisticsScope.MONTH.equals(scope)) {
            return month.toUpperCase();
        }

        return scope.name();
    }

}

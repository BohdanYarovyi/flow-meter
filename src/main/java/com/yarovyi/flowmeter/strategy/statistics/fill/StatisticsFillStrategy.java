package com.yarovyi.flowmeter.strategy.statistics.fill;

import com.yarovyi.flowmeter.dto.stat.StatInterval;
import com.yarovyi.flowmeter.dto.stat.StatPoint;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public interface StatisticsFillStrategy {

    StatInterval fillGaps(StatInterval statInterval);

    default void addAbsentDays(int requiredDaysNumber, List<StatPoint> points) {
        LocalDate monthAgo = LocalDate.now().minusDays(requiredDaysNumber - 1);
        Set<LocalDate> existDates = points.stream()
                .map(StatPoint::date)
                .collect(Collectors.toSet());

        for (int i = 0; i < requiredDaysNumber; i++) {
            StatPoint emptyPoint = StatPoint.empty(monthAgo.plusDays(i));

            if (!existDates.contains(emptyPoint.date())) {
                points.add(emptyPoint);
            }
        }
    }

}

package com.yarovyi.flowmeter.strategy.statistics.fill;

import com.yarovyi.flowmeter.dto.stat.StatInterval;
import com.yarovyi.flowmeter.dto.stat.StatPoint;

import java.util.ArrayList;
import java.util.List;

public class StatisticsLastMonthFillStrategy implements StatisticsFillStrategy {

    @Override
    public StatInterval fillGaps(StatInterval statInterval) {
        if (statInterval == null)
            throw new IllegalArgumentException("statInterval is not present");

        int daysInMonth = 31;
        List<StatPoint> points = new ArrayList<>(statInterval.points());

        addAbsentDays(daysInMonth, points);

        points.sort(StatPoint::compareTo);
        return StatInterval.of(statInterval, points);
    }

}

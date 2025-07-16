package com.yarovyi.flowmeter.strategy.statistics.fill;

import com.yarovyi.flowmeter.dto.stat.StatInterval;
import com.yarovyi.flowmeter.dto.stat.StatPoint;
import com.yarovyi.flowmeter.entity.view.EfficiencyView;

import java.util.ArrayList;
import java.util.List;

public class StatisticsLastMonthFillStrategy implements StatisticsFillStrategy {

    @Override
    public StatInterval fillGaps(StatInterval statInterval, List<EfficiencyView> efficiencyViews) {
        if (statInterval == null)
            throw new IllegalArgumentException("statInterval is not present");

        int daysInMonth = 31;
        List<StatPoint> points = mapFromEfficiencyView(efficiencyViews);

        addAbsentDays(daysInMonth, points);

        points.sort(StatPoint::compareTo);
        return StatInterval.of(statInterval, points);
    }

}

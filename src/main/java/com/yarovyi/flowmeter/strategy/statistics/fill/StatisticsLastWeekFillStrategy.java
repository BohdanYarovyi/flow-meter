package com.yarovyi.flowmeter.strategy.statistics.fill;

import com.yarovyi.flowmeter.dto.stat.StatInterval;
import com.yarovyi.flowmeter.dto.stat.StatPoint;
import com.yarovyi.flowmeter.entity.view.EfficiencyView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class StatisticsLastWeekFillStrategy implements StatisticsFillStrategy {

    @Override
    public StatInterval fillGaps(StatInterval statInterval, List<EfficiencyView> efficiencyViews) {
        if (statInterval == null)
            throw new IllegalArgumentException("statInterval is not present");

        int daysInWeek = 7;
        List<StatPoint> points = mapFromEfficiencyView(efficiencyViews);

        addAbsentDays(daysInWeek, points);

        points.sort(StatPoint::compareTo);
        return StatInterval.of(statInterval, points);
    }

}

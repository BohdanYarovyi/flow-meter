package com.yarovyi.flowmeter.strategy.statistics.fill;

import com.yarovyi.flowmeter.dto.stat.StatInterval;
import com.yarovyi.flowmeter.dto.stat.StatPoint;
import com.yarovyi.flowmeter.entity.view.EfficiencyView;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class StatisticsLastYearFillStrategy implements StatisticsFillStrategy {

    @Override
    public StatInterval fillGaps(StatInterval statInterval, List<EfficiencyView> efficiencyViews) {
        correctAverageValues(efficiencyViews);
        List<StatPoint> points = mapFromEfficiencyView(efficiencyViews);
        addAbsentMonths(points);
        points.sort(StatPoint::compareTo);

        return StatInterval.of(statInterval, points);
    }

    private void correctAverageValues(List<EfficiencyView> efficiencyViews) {
        for (EfficiencyView efficiency : efficiencyViews) {
            LocalDate fullDate = efficiency.getFullDate();
            int lengthOfMonth = YearMonth.from(fullDate).lengthOfMonth();
            int averagePercent = efficiency.getAveragePercent();
            int averageCount = efficiency.getAverageCount();

            efficiency.setAveragePercent(averageCount * averagePercent / lengthOfMonth);
        }
    }

    // можна покращити через Set, але нехай буде так
    private void addAbsentMonths(List<StatPoint> points) {
        List<LocalDate> existsDates = points.stream()
                .map(StatPoint::date)
                .toList();

        LocalDate yearAgo = LocalDate.now()
                .minusMonths(11)
                .withDayOfMonth(1);

        for (int i = 0; i < 12; i++) {
            LocalDate month = yearAgo.plusMonths(i);
            boolean isNotExist = existsDates.stream().noneMatch(e -> compareMonths(e, month));

            if (isNotExist) {
                points.add(StatPoint.empty(month));
            }
        }
    }

    private boolean compareMonths(LocalDate date1, LocalDate date2) {
        YearMonth m1 = YearMonth.from(date1);
        YearMonth m2 = YearMonth.from(date2);

        return m1.equals(m2);
    }

}

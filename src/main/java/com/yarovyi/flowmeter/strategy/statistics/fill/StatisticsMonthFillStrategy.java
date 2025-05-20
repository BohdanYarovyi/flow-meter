package com.yarovyi.flowmeter.strategy.statistics.fill;

import com.yarovyi.flowmeter.dto.stat.StatInterval;
import com.yarovyi.flowmeter.dto.stat.StatPoint;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;

public class StatisticsMonthFillStrategy implements StatisticsFillStrategy {

    @Override
    public StatInterval fillGaps(StatInterval statInterval) {
        YearMonth yearMonth = getYearMonth(statInterval);
        List<StatPoint> efficiency = statInterval.points();

        StatPoint[] temp = new StatPoint[yearMonth.lengthOfMonth()];
        for (StatPoint point : efficiency) {
            if (point != null) {
                int dayOfMonth = point.date().getDayOfMonth();
                temp[dayOfMonth - 1] = point;
            }
        }

        for (int i = 0; i < temp.length; i++) {
            if (temp[i] == null) {
                Month month = Month.valueOf(statInterval.interval().toUpperCase());
                LocalDate date = LocalDate.of(statInterval.year(), month, i + 1);
                temp[i] = StatPoint.empty(date);
            }
        }

        return new StatInterval(
                statInterval.interval(),
                statInterval.flowTitle(),
                statInterval.year(),
                List.of(temp)
        );
    }

    private YearMonth getYearMonth(StatInterval statInterval) {
        Month month = Month.valueOf(statInterval.interval().toUpperCase());

        return YearMonth.of(statInterval.year(), month);
    }

}

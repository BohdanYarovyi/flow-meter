package com.yarovyi.flowmeter.strategy.statistics.fill;

import com.yarovyi.flowmeter.dto.stat.StatInterval;
import com.yarovyi.flowmeter.dto.stat.StatPoint;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public interface StatisticsFillStrategy {

    StatInterval fillGaps(StatInterval statInterval);

    /**
     * Adds missing {@link StatPoint} entries to the provided list of points,
     * ensuring it contains a complete sequence of dates for the specified number of days.
     * <p>
     * This method starts from {@code LocalDate.now().minusDays(requiredDaysNumber - 1)} and
     * ensures there is a {@link StatPoint} for each date up to today. If a date is missing,
     * a default empty {@code StatPoint} is added for it using {@code StatPoint.empty(LocalDate)}.
     * <p>
     * Existing points are left unchanged.
     *
     * @param requiredDaysNumber the total number of days to cover, counting backwards from today
     * @param points the list of existing {@code StatPoint} entries to be checked and filled if needed
     */
    default void addAbsentDays(int requiredDaysNumber, List<StatPoint> points) {
        LocalDate startDay = LocalDate.now().minusDays(requiredDaysNumber - 1);
        Set<LocalDate> existDates = points.stream()
                .map(StatPoint::date)
                .collect(Collectors.toSet());

        for (int i = 0; i < requiredDaysNumber; i++) {
            StatPoint emptyPoint = StatPoint.empty(startDay.plusDays(i));

            if (!existDates.contains(emptyPoint.date())) {
                points.add(emptyPoint);
            }
        }
    }

}

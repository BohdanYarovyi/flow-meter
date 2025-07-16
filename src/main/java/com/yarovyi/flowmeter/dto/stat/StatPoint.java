package com.yarovyi.flowmeter.dto.stat;

import com.yarovyi.flowmeter.entity.view.EfficiencyView;

import java.time.LocalDate;

public record StatPoint(LocalDate date, int percentage) implements Comparable<StatPoint> {

    public static StatPoint empty(LocalDate date) {
        return new StatPoint(date, 0);
    }

    public static StatPoint of(EfficiencyView efficiencyView) {
        return new StatPoint(efficiencyView.getFullDate(), efficiencyView.getAveragePercent());
    }

    @Override
    public int compareTo(StatPoint other) {
        return this.date.compareTo(other.date);
    }

}

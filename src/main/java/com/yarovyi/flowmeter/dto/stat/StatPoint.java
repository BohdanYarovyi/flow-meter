package com.yarovyi.flowmeter.dto.stat;

import java.time.LocalDate;

public record StatPoint(LocalDate date, int percentage) implements Comparable<StatPoint> {

    public static StatPoint empty(LocalDate date) {
        return new StatPoint(date, 0);
    }

    @Override
    public int compareTo(StatPoint other) {
        return this.date.compareTo(other.date);
    }

}

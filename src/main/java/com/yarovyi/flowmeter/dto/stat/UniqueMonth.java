package com.yarovyi.flowmeter.dto.stat;

import java.time.Month;
import java.time.YearMonth;

public record UniqueMonth(int year, String month) implements Comparable<UniqueMonth> {

    @Override
    public int compareTo(UniqueMonth other) {
        return toYearMonth().compareTo(other.toYearMonth());
    }

    private YearMonth toYearMonth() {
        Month tMonth = Month.valueOf(month.toUpperCase());

        return YearMonth.of(year, tMonth);
    }

}

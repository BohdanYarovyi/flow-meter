package com.yarovyi.flowmeter.dto.stat;

import java.time.Month;
import java.time.YearMonth;
import java.util.Objects;

public record UniqueMonth(int year, String month) implements Comparable<UniqueMonth> {

    @Override
    public int compareTo(UniqueMonth other) {
        return toYearMonth().compareTo(other.toYearMonth());
    }

    private YearMonth toYearMonth() {
        Month tMonth = Month.valueOf(month.toUpperCase());

        return YearMonth.of(year, tMonth);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UniqueMonth that = (UniqueMonth) o;

        return year == that.year && Objects.equals(month, that.month);
    }

    @Override
    public int hashCode() {
        return Objects.hash(year, month);
    }
}

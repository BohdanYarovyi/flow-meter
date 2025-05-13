package com.yarovyi.flowmeter.service;

import com.yarovyi.flowmeter.domain.flow.Flow;
import com.yarovyi.flowmeter.domain.flow.Step;
import com.yarovyi.flowmeter.entity.dto.UniqueMonth;
import com.yarovyi.flowmeter.entity.exception.SubentityNotFoundException;
import com.yarovyi.flowmeter.repository.FlowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.time.Year;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class StatisticsServiceImpl implements StatisticsService {
    private final FlowRepository flowRepository;

    @Override
    public Set<UniqueMonth> getSortedUniqueMonths(Long flowId) {
        if (flowId == null)
            throw new NullPointerException();

        Flow flow = this.flowRepository
                .findById(flowId)
                .orElseThrow(() -> new SubentityNotFoundException(Flow.class));

        List<Step> steps = flow.getSteps();

        return steps.stream()
                .map(Step::getDay)
                .map(day -> new UniqueMonth(day.getYear(), day.getMonth().name()))
                .collect(Collectors.toCollection(() -> new TreeSet<>(new MonthsComparator())));
    }

    private static class MonthsComparator implements Comparator<UniqueMonth> {
        @Override
        public int compare(UniqueMonth m1, UniqueMonth m2) {
            if (m1.year() != m2.year()) {
                return m2.year() - m1.year();
            } else {
                return compareMonths(m2.month(), m1.month());
            }
        }

        private int compareMonths(String month1, String month2) {
            Month m1 = Month.valueOf(month1);
            Month m2 = Month.valueOf(month2);

            return m1.compareTo(m2);
        }
    }


}

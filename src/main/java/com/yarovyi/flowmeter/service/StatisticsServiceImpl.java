package com.yarovyi.flowmeter.service;

import com.yarovyi.flowmeter.dto.stat.StatInterval;
import com.yarovyi.flowmeter.dto.stat.StatParams;
import com.yarovyi.flowmeter.dto.stat.UniqueMonth;
import com.yarovyi.flowmeter.entity.flow.Flow;
import com.yarovyi.flowmeter.entity.flow.Step;
import com.yarovyi.flowmeter.entity.view.EfficiencyView;
import com.yarovyi.flowmeter.exception.InvalidParametersException;
import com.yarovyi.flowmeter.exception.SubentityNotFoundException;
import com.yarovyi.flowmeter.repository.FlowRepository;
import com.yarovyi.flowmeter.repository.StatisticsRepository;
import com.yarovyi.flowmeter.strategy.statistics.StatisticsFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
import java.time.Month;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class StatisticsServiceImpl implements StatisticsService {
    private final FlowRepository flowRepository;
    private final StatisticsRepository statisticsRepository;
    private final StatisticsFactory statisticsFactory;


    @Override
    public Set<UniqueMonth> getSortedUniqueMonths(Long flowId) {
        if (flowId == null) {
            throw new IllegalArgumentException("flowId is not present");
        }

        Flow flow = this.flowRepository
                .findById(flowId)
                .orElseThrow(() -> new SubentityNotFoundException(Flow.class));

        List<Step> steps = flow.getSteps();

        return steps.stream()
                .map(Step::getDay)
                .map(day -> new UniqueMonth(day.getYear(), day.getMonth().name()))
                .collect(Collectors.toCollection(() -> new TreeSet<>(new MonthsComparator())));
    }


    @Override
    public StatInterval getStatisticsForMonth(Long flowId, int year, String month) {
        if (flowId == null || month == null ) {
            throw new IllegalArgumentException("Parameters are not present");
        }

        this.validateMonth(month);
        var params = new StatParams(StatisticsScope.MONTH, flowId, year, month);
        return this.statisticsFactory.prepareStatistics(params, this.statisticsRepository);
    }


    @Override
    public StatInterval getStatisticsForLastWeek(Long flowId) {
        if (flowId == null) {
            throw new IllegalArgumentException("flowId is not present");
        }

        var params = new StatParams(StatisticsScope.LAST_WEEK, flowId, null, null);
        return this.statisticsFactory.prepareStatistics(params, this.statisticsRepository);
    }


    @Override
    public StatInterval getStatisticsForLastMonth(Long flowId) {
        if (flowId == null) {
            throw new IllegalArgumentException("flowId is not present");
        }

        var params = new StatParams(StatisticsScope.LAST_MONTH, flowId, null, null);
        return this.statisticsFactory.prepareStatistics(params, this.statisticsRepository);
    }


    @Override
    public StatInterval getStatisticsForLastYear(Long flowId) {
        if (flowId == null) {
            throw new IllegalArgumentException("flowId is not present");
        }

        var params = new StatParams(StatisticsScope.LAST_YEAR, flowId, null, null);
        return this.statisticsFactory.prepareStatistics(params, this.statisticsRepository);
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
            Month m1 = Month.valueOf(month1.toUpperCase());
            Month m2 = Month.valueOf(month2.toUpperCase());

            return m1.compareTo(m2);
        }
    }


    private void validateMonth(String month) {
        try {
            Month.valueOf(month.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidParametersException("Month parameter is invalid: " + month);
        }
    }


}

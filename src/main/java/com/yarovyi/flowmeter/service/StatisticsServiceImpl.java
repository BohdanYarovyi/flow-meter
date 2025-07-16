package com.yarovyi.flowmeter.service;

import com.yarovyi.flowmeter.dto.stat.StatInterval;
import com.yarovyi.flowmeter.dto.stat.StatParams;
import com.yarovyi.flowmeter.dto.stat.UniqueMonth;
import com.yarovyi.flowmeter.entity.flow.Flow;
import com.yarovyi.flowmeter.entity.flow.Step;
import com.yarovyi.flowmeter.exception.InvalidParametersException;
import com.yarovyi.flowmeter.exception.SubentityNotFoundException;
import com.yarovyi.flowmeter.repository.FlowRepository;
import com.yarovyi.flowmeter.repository.StatisticsRepository;
import com.yarovyi.flowmeter.strategy.statistics.StatisticsFactory;
import com.yarovyi.flowmeter.strategy.statistics.StatisticsScope;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Month;
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


    @Transactional(readOnly = true)
    @Override
    public Set<UniqueMonth> getSortedUniqueMonths(Long flowId) {
        if (flowId == null) {
            throw new IllegalArgumentException("Parameter 'flowId' is null");
        }

        Flow flow = flowRepository
                .findById(flowId)
                .orElseThrow(() -> new SubentityNotFoundException(Flow.class));

        return flow
                .getSteps()
                .stream()
                .map(Step::getDay)
                .map(day -> new UniqueMonth(day.getYear(), day.getMonth().name()))
                .collect(Collectors.toCollection(TreeSet::new));
    }


    @Transactional(readOnly = true)
    @Override
    public StatInterval getStatisticsForMonth(Long flowId, int year, String month) {
        if (flowId == null || month == null ) {
            throw new IllegalArgumentException("Parameters are not present");
        }

        validateMonth(month);
        var params = new StatParams(StatisticsScope.MONTH, flowId, year, month);
        return statisticsFactory.prepareStatistics(params, statisticsRepository);
    }

    private void validateMonth(String month) {
        try {
            Month.valueOf(month.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidParametersException("Month parameter is invalid: " + month);
        }
    }


    @Transactional(readOnly = true)
    @Override
    public StatInterval getStatisticsForLastWeek(Long flowId) {
        if (flowId == null) {
            throw new IllegalArgumentException("flowId is not present");
        }

        var params = new StatParams(StatisticsScope.LAST_WEEK, flowId, null, null);
        return statisticsFactory.prepareStatistics(params, statisticsRepository);
    }


    @Transactional(readOnly = true)
    @Override
    public StatInterval getStatisticsForLastMonth(Long flowId) {
        if (flowId == null) {
            throw new IllegalArgumentException("flowId is not present");
        }

        var params = new StatParams(StatisticsScope.LAST_MONTH, flowId, null, null);
        return statisticsFactory.prepareStatistics(params, statisticsRepository);
    }


    @Transactional(readOnly = true)
    @Override
    public StatInterval getStatisticsForLastYear(Long flowId) {
        if (flowId == null) {
            throw new IllegalArgumentException("flowId is not present");
        }

        var params = new StatParams(StatisticsScope.LAST_YEAR, flowId, null, null);
        return this.statisticsFactory.prepareStatistics(params, this.statisticsRepository);
    }

}

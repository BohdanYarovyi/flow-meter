package com.yarovyi.flowmeter.strategy.statistics;

import com.yarovyi.flowmeter.dto.stat.StatInterval;
import com.yarovyi.flowmeter.dto.stat.StatParams;
import com.yarovyi.flowmeter.dto.stat.StatPoint;
import com.yarovyi.flowmeter.entity.view.EfficiencyView;
import com.yarovyi.flowmeter.repository.StatisticsRepository;
import com.yarovyi.flowmeter.strategy.statistics.fill.*;
import com.yarovyi.flowmeter.strategy.statistics.query.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class StatisticsFactoryImpl implements StatisticsFactory {
    private final Map<StatisticsScope, StatisticsQueryStrategy> queryStrategies;
    private final Map<StatisticsScope, StatisticsFillStrategy> fillStrategies;

    public StatisticsFactoryImpl() {
        this.queryStrategies = initQueryStrategies();
        this.fillStrategies = initFillStrategies();
    }

    private Map<StatisticsScope, StatisticsQueryStrategy> initQueryStrategies() {
        return Map.of(
                StatisticsScope.MONTH, new StatisticsMonthQueryStrategy(),
                StatisticsScope.LAST_WEEK, new StatisticsLastWeekQueryStrategy(),
                StatisticsScope.LAST_MONTH, new StatisticsLastMonthQueryStrategy(),
                StatisticsScope.LAST_YEAR, new StatisticsLastYearQueryStrategy()
        );
    }

    private Map<StatisticsScope, StatisticsFillStrategy> initFillStrategies() {
        return Map.of(
                StatisticsScope.MONTH, new StatisticsMonthFillStrategy(),
                StatisticsScope.LAST_WEEK, new StatisticsLastWeekFillStrategy(),
                StatisticsScope.LAST_MONTH, new StatisticsLastMonthFillStrategy(),
                StatisticsScope.LAST_YEAR, new StatisticsLastYearFillStrategy()
        );
    }


    @Override
    public StatInterval prepareStatistics(StatParams params, StatisticsRepository statisticsSource) {
        if (params == null || statisticsSource == null) {
            throw new IllegalArgumentException("Parameters are not present");
        }

        StatisticsQueryStrategy queryStrategy = this.queryStrategies.get(params.scope());
        StatisticsFillStrategy fillStrategy = this.fillStrategies.get(params.scope());
        if (queryStrategy == null || fillStrategy == null) {
            throw new UnsupportedOperationException("Not supported scope: " + params.scope());
        }

        StatInterval statInterval = createStatInterval(params, statisticsSource, queryStrategy);
        return fillStrategy.fillGaps(statInterval);
    }


    private StatInterval createStatInterval(StatParams params,
                                            StatisticsRepository statisticsSource,
                                            StatisticsQueryStrategy queryStrategy) {
        Integer year = params.getYearDependsScope();
        String interval = params.getIntervalDependsScope();
        String flowTitle = statisticsSource.getFlowTitleByFlowId(params.flowId());

        List<EfficiencyView> exists = queryStrategy.fetch(params, statisticsSource);
        if (exists.isEmpty()) {
            return new StatInterval(interval, flowTitle, year, List.of());
        } else {
            List<StatPoint> statPoints = exists.stream()
                    .map(v -> new StatPoint(v.getFullDate(), v.getAveragePercent()))
                    .toList();

            return new StatInterval(interval, flowTitle, year, statPoints);
        }
    }


}

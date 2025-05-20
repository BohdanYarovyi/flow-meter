package com.yarovyi.flowmeter.strategy.statistics.query;

import com.yarovyi.flowmeter.dto.stat.StatParams;
import com.yarovyi.flowmeter.entity.view.EfficiencyView;
import com.yarovyi.flowmeter.repository.StatisticsRepository;

import java.util.List;

public class StatisticsLastMonthQueryStrategy implements StatisticsQueryStrategy {

    @Override
    public List<EfficiencyView> fetch(StatParams params, StatisticsRepository source) {
        return source.getEfficient_lastMonth(params.flowId());
    }

}

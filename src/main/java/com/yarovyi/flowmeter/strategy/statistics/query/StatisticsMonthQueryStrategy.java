package com.yarovyi.flowmeter.strategy.statistics.query;

import com.yarovyi.flowmeter.dto.stat.StatParams;
import com.yarovyi.flowmeter.entity.view.EfficiencyView;
import com.yarovyi.flowmeter.repository.StatisticsRepository;

import java.util.List;

public class StatisticsMonthQueryStrategy implements StatisticsQueryStrategy {

    @Override
    public List<EfficiencyView> fetch(StatParams params, StatisticsRepository source) {
        Long flowId = params.flowId();
        Integer year = params.year();
        String month = params.month().toUpperCase();

        return source.getEfficiency_month(flowId, year, month);
    }

}

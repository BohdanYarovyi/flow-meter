package com.yarovyi.flowmeter.strategy.statistics.query;

import com.yarovyi.flowmeter.dto.stat.StatParams;
import com.yarovyi.flowmeter.entity.view.EfficiencyView;
import com.yarovyi.flowmeter.repository.StatisticsRepository;

import java.util.List;

public interface StatisticsQueryStrategy {

    List<EfficiencyView> fetch(StatParams params, StatisticsRepository source);

}

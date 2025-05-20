package com.yarovyi.flowmeter.strategy.statistics;

import com.yarovyi.flowmeter.dto.stat.StatInterval;
import com.yarovyi.flowmeter.dto.stat.StatParams;
import com.yarovyi.flowmeter.repository.StatisticsRepository;

public interface StatisticsFactory {

    StatInterval prepareStatistics(StatParams params, StatisticsRepository statisticsSource);

}

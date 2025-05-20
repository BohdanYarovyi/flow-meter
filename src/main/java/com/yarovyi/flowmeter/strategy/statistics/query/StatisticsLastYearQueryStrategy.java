package com.yarovyi.flowmeter.strategy.statistics.query;

import com.yarovyi.flowmeter.dto.stat.StatParams;
import com.yarovyi.flowmeter.entity.view.EfficiencyView;
import com.yarovyi.flowmeter.entity.view.EfficiencyViewProjection;
import com.yarovyi.flowmeter.repository.StatisticsRepository;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

public class StatisticsLastYearQueryStrategy implements StatisticsQueryStrategy {


    @Override
    public List<EfficiencyView> fetch(StatParams params, StatisticsRepository source) {
        List<EfficiencyViewProjection> lastYear = source.getEfficient_lastYear(params.flowId());

        return mapToEfficientView(lastYear);
    }


    // convert EfficiencyViewProjection -> EfficiencyView
    private List<EfficiencyView> mapToEfficientView(List<EfficiencyViewProjection> lastYear) {
        List<EfficiencyView> lastYearEfficientViews = new ArrayList<>();

        for (EfficiencyViewProjection projection : lastYear) {
            Month month = Month.valueOf(projection.getMonth().toUpperCase());
            LocalDate fullDate = LocalDate.of(projection.getYear(), month, 1);

            var efficientView = EfficiencyView.builder()
                    .flowId(projection.getFlowId())
                    .flowTitle(projection.getFlowTitle())
                    .year(projection.getYear())
                    .month(projection.getMonth())
                    .fullDate(fullDate)
                    .averagePercent(projection.getAveragePercent())
                    .build();

            lastYearEfficientViews.add(efficientView);
        }

        return lastYearEfficientViews;
    }


}

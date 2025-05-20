package com.yarovyi.flowmeter.entity.view;


/**
 * Projection created only for fetching statistics for last_year from database.
 */
public interface EfficiencyViewProjection {
    Long getFlowId();
    String getFlowTitle();
    Integer getYear();
    String getMonth();
    Integer getAveragePercent();
}

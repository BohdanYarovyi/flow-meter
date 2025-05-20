package com.yarovyi.flowmeter.dto.stat;

import java.util.List;


/**
 * StatInterval an object that stores statistics information. Uses for transfer for frontend.
 *
 * @param interval the interval of statistics (e.g., "monthly", "weekly", "last moth")
 * @param flowTitle is a flow title for which statistics are taken
 * @param year optional; used to specify the year if the interval is a specific month
 * @param points the list of statistical points used for graph visualization
 */
public record StatInterval(
        String interval,
        String flowTitle,
        Integer year,
        List<StatPoint> points
) {

    public static StatInterval of(StatInterval statInterval, List<StatPoint> points) {
        return new StatInterval(
                statInterval.interval(),
                statInterval.flowTitle(),
                statInterval.year(),
                points
        );
    }
}

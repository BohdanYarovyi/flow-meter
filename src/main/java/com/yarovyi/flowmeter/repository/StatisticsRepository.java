package com.yarovyi.flowmeter.repository;

import com.yarovyi.flowmeter.entity.view.EfficiencyView;
import com.yarovyi.flowmeter.entity.view.EfficiencyViewProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatisticsRepository extends JpaRepository<EfficiencyView, Long> {

    @Query(value = """
            SELECT *
            FROM v_statistics_data v
            WHERE v.flow_id = :flowId
                  AND v.year = :year
                  AND v.month = :month;
            """,
            nativeQuery = true)
    List<EfficiencyView> getEfficiency_month(@Param("flowId") Long flowId,
                                             @Param("year") int year,
                                             @Param("month") String month);


    @Query(value = """
            SELECT *
            FROM v_statistics_data v
            WHERE flow_id = :flowId
                AND v.full_date BETWEEN CURRENT_DATE - interval '6 days' AND CURRENT_DATE;
            """,
            nativeQuery = true)
    List<EfficiencyView> getEfficiency_lastWeek(@Param("flowId") Long flowId);


    @Query(value = """
        SELECT *
        FROM v_statistics_data v
        WHERE flow_id = :flowId
            AND v.full_date BETWEEN CURRENT_DATE - interval '31 days' AND CURRENT_DATE;
    """,
            nativeQuery = true)
    List<EfficiencyView> getEfficient_lastMonth(@Param("flowId") Long flowId);


    @Query(value = """
    SELECT
        v.flow_id as flow_id,
        v.flow_title as flow_title,
        v.year as year,
        v.month as month,
        AVG(v.average_percent) as average_percent
    FROM v_statistics_data v
    WHERE v.flow_id = :flowId
        AND v.full_date BETWEEN CURRENT_DATE - interval '11 months' AND CURRENT_DATE
    GROUP BY
        v.flow_id,
        v.flow_title,
        v.year,
        v.month;
    """,nativeQuery = true)
    List<EfficiencyViewProjection> getEfficient_lastYear(@Param("flowId") Long flowId);


    @Query(value = """
            SELECT v.flow_title
            FROM v_statistics_data v
            WHERE v.flow_id = :flowId
            LIMIT 1;
            """,
            nativeQuery = true)
    String getFlowTitleByFlowId(@Param("flowId") Long flowId);


}

CREATE OR REPLACE VIEW v_statistics_data AS
    SELECT  f.id AS flow_id,
            s.id AS step_id,
            f.c_title AS flow_title,
            EXTRACT(YEAR FROM s.c_day) as year,
            UPPER(TO_CHAR(s.c_day, 'FMMonth')) as month,
            EXTRACT(DAY FROM s.c_day) as day,
            s.c_day as full_date,
            ROUND(AVG(c.c_percent))::int AS average_percent
    FROM t_flows AS f
    JOIN t_steps s ON f.id = s.flow_id
    JOIN t_cases c ON s.id = c.step_id
    WHERE
        NOT f.c_deleted
        AND NOT s.c_deleted
        AND NOT c.c_deleted
        AND c.c_counting
    GROUP BY
        f.id,
        s.id,
        f.c_title,
        s.c_day;
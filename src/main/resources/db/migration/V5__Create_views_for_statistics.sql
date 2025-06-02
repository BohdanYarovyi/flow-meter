CREATE OR REPLACE VIEW v_statistics_data AS
    SELECT  f.id AS flow_id,
            s.id AS step_id,
            f.title AS flow_title,
            EXTRACT(YEAR FROM s.day) as year,
            UPPER(TO_CHAR(s.day, 'FMMonth')) as month,
            EXTRACT(DAY FROM s.day) as day,
            s.day as full_date,
            ROUND(AVG(c.percent))::int AS average_percent
    FROM flow AS f
    JOIN step s ON f.id = s.flow_id
    JOIN cases c ON s.id = c.step_id
    WHERE
        NOT f.deleted
        AND NOT s.deleted
        AND NOT c.deleted
        AND c.counting
    GROUP BY
        f.id,
        s.id,
        f.title,
        s.day;
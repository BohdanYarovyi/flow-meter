INSERT INTO t_flows (account_id, c_title, c_description, c_target_percentage, c_created_at, c_updated_at, c_deleted)
VALUES  (1, 'SQL Fundamentals', 'SQL Fundamental, HTTP/HTTPSm, conspecting', 70, CURRENT_TIMESTAMP, current_timestamp, false),
        (1, 'Driving', 'Everyday learn road rules', 70, CURRENT_TIMESTAMP, current_timestamp, false),
        (1, 'Gym', 'Do gym, improve physic', 70, CURRENT_TIMESTAMP, current_timestamp, false);

INSERT INTO t_steps (flow_id, c_day, c_created_at, c_updated_at, c_deleted)
VALUES (1, '2025-03-20', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
       (1, '2025-03-21', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
       (1, '2025-03-22', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
       (2, '2025-02-25', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
       (2, '2025-03-5', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
       (2, '2025-03-15', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
       (2, '2025-03-25', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
       (3, '2025-03-24', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
       (3, '2025-03-28', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false);

INSERT INTO t_cases (step_id, c_text, c_percent, c_created_at, c_updated_at, c_deleted)
VALUES (1, 'SQL Fundamentals', 90,  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
       (1, 'Conspect SQL Fundamentals', 85,  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
       (1, 'Programming Flowmeter, created login', 100,  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
       (2, 'SQL Fundamentals', 70,  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
       (2, 'Conspect SQL Fundamentals a little bit', 50,  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
       (2, 'Programming Flowmeter, created registration', 90,  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
       (3, 'SQL Fundamentals', 50,  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
       (3, 'Conspect SQL Fundamentals', 50,  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
       (3, 'Programming Flowmeter, created web pages for registration and login, main page', 95,  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
       (4, 'Drive around city 40 minutes', 50,  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
       (4, 'Paralel parking', 50,  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
       (5, 'Drive to grocery shop', 50,  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
       (5, 'Get a circle around district', 50,  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
       (6, 'Driving in the Alexandria', 50,  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
       (6, 'Night drive', 50,  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
       (7, 'Drive to the Kyiv', 50,  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
       (7, 'Drive to the Kyiv and in the city', 50,  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
       (8, 'Do pectoral muscles, bench press', 50,  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
       (9, 'Front biceps, inverse biceps', 50,  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false);

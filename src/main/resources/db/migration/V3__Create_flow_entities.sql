CREATE TABLE IF NOT EXISTS t_flows
(
    id                  bigserial,
    account_id          bigint       not null,
    c_title             varchar(100) not null,
    c_description       varchar(1000),
    c_target_percentage int          not null CHECK (c_target_percentage <= 100 AND t_flows.c_target_percentage > 0),
    c_created_at        timestamp    not null,
    c_updated_at        timestamp    not null,
    c_deleted           boolean      not null,
    PRIMARY KEY (id),
    FOREIGN KEY (account_id) REFERENCES t_accounts (id)
);

CREATE TABLE IF NOT EXISTS t_steps
(
    id           bigserial,
    flow_id      bigint    not null,
    c_day        date not null,
    c_created_at timestamp not null,
    c_updated_at timestamp not null,
    c_deleted    boolean   not null,
    PRIMARY KEY (id),
    FOREIGN KEY (flow_id) REFERENCES t_flows (id)
);

CREATE TABLE IF NOT EXISTS t_cases
(
    id           bigserial,
    step_id      bigint       not null,
    c_text       varchar(255) not null,
    c_percent    int,
    c_created_at timestamp    not null,
    c_updated_at timestamp    not null,
    c_deleted    boolean      not null,
    PRIMARY KEY (id),
    FOREIGN KEY (step_id) REFERENCES t_cases(id)
);

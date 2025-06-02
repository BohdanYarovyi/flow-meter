CREATE TABLE IF NOT EXISTS flow
(
    id                bigserial,
    account_id        bigint       not null,
    title             varchar(100) not null,
    description       varchar(1000),
    target_percentage int          not null CHECK (target_percentage <= 100 AND target_percentage >= 0),
    created_at        timestamp    not null,
    updated_at        timestamp,
    deleted           boolean      not null,
    PRIMARY KEY (id),
    FOREIGN KEY (account_id) REFERENCES account (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS step
(
    id         bigserial,
    flow_id    bigint    not null,
    day        date      not null,
    created_at timestamp not null,
    updated_at timestamp,
    deleted    boolean   not null,
    PRIMARY KEY (id),
    FOREIGN KEY (flow_id) REFERENCES flow (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS cases
(
    id         bigserial,
    step_id    bigint       not null,
    text       varchar(255) not null,
    percent    int,
    counting   boolean      not null,
    created_at timestamp    not null,
    updated_at timestamp,
    deleted    boolean      not null,
    PRIMARY KEY (id),
    FOREIGN KEY (step_id) REFERENCES step (id) ON DELETE CASCADE
);

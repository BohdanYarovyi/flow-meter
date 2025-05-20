DROP TABLE IF EXISTS t_accounts_roles;
DROP TABLE IF EXISTS t_roles;
DROP TABLE IF EXISTS t_accounts;

CREATE TABLE IF NOT EXISTS t_accounts
(
    id              bigserial,
    c_created_at    timestamp    not null,
    c_updated_at    timestamp,
    c_deleted       boolean      not null,
    c_login         varchar(100) not null unique,
    c_email         varchar(100) not null unique,
    c_password      varchar(100) not null,
    c_firstname     varchar(100),
    c_lastname      varchar(100),
    c_patronymic    varchar(100),
    c_date_of_birth date,
    c_phone         varchar(100),
    primary key (id)
);

CREATE TABLE IF NOT EXISTS t_roles
(
    id     bigserial,
    c_name varchar(255) not null unique,
    primary key (id)
);

CREATE TABLE IF NOT EXISTS t_accounts_roles
(
    account_id bigint not null,
    role_id    bigint not null,
    primary key (account_id, role_id),
    foreign key (account_id) references t_accounts (id) on delete cascade,
    foreign key (role_id) references t_roles (id) on delete cascade
);
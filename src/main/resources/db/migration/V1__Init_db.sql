DROP TABLE IF EXISTS account_role;
DROP TABLE IF EXISTS role;
DROP TABLE IF EXISTS account;

CREATE TABLE IF NOT EXISTS account
(
    id            bigserial,
    created_at    timestamp    not null,
    updated_at    timestamp,
    deleted       boolean      not null,
    login         varchar(100) not null unique,
    email         varchar(100) not null unique,
    password      varchar(100) not null,
    firstname     varchar(100),
    lastname      varchar(100),
    patronymic    varchar(100),
    date_of_birth date,
    phone         varchar(100),
    primary key (id)
);

CREATE TABLE IF NOT EXISTS role
(
    id   bigserial,
    name varchar(255) not null unique,
    primary key (id)
);

CREATE TABLE IF NOT EXISTS account_role
(
    account_id bigint not null,
    role_id    bigint not null,
    primary key (account_id, role_id),
    foreign key (account_id) references account (id) on delete cascade,
    foreign key (role_id) references role (id) on delete cascade
);
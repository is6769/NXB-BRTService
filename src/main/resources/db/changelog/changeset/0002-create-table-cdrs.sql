--liquibase formatted sql
--changeset is6769:0002-create-table-cdrs
create table if not exists cdrs(
    id                  bigserial         PRIMARY KEY,
    call_type           varchar(200)   not null,
    caller_number       varchar(200)   not null,
    called_number       varchar(200)   not null,
    start_date_time     timestamp       not null,
    finish_date_time    timestamp       not null,
    consumed_status     varchar(200)   not null
)

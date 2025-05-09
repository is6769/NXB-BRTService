--liquibase formatted sql
--changeset is6769:0002-create-table-cdrs
create table if not exists cdrs(
    id                  BIGSERIAL       PRIMARY KEY,
    call_type           VARCHAR(3)      NOT NULL,
    serviced_msisdn     VARCHAR(50)     NOT NULL,
    other_msisdn        VARCHAR(50)     NOT NULL,
    start_date_time     TIMESTAMP       NOT NULL,
    finish_date_time    TIMESTAMP       NOT NULL
)

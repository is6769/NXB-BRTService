--liquibase formatted sql
--changeset is6769:0001-create-table-subscribers
create table if not exists subscribers(
    id              BIGSERIAL       PRIMARY KEY,
    msisdn          VARCHAR(50)     NOT NULL,
    first_name      TEXT            NOT NULL,
    second_name     TEXT,
    surname         TEXT            NOT NULL,
    tariff_id       BIGINT,
    balance         NUMERIC         DEFAULT 100,
    registered_at   TIMESTAMP       NOT NULL
)

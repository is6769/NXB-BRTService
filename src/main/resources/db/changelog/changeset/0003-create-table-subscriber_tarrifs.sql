--liquibase formatted sql
--changeset is6769:0003-create-table-subscriber_tariffs
create table if not exists subscriber_tariffs(
    id                  BIGSERIAL       PRIMARY KEY,
    subscriber_id       BIGINT          NOT NULL,
    tariff_id           BIGINT          NOT NULL,
    period_start_date   TIMESTAMP       NOT NULL,
    next_payment_date   TIMESTAMP       NOT NULL,
    minutes_remaining   NUMERIC         NOT NULL,
    status              VARCHAR(50)     NOT NULL
)

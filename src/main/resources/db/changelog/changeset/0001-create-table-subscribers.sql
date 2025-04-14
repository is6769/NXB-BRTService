--liquibase formatted sql
--changeset is6769:0001-create-table-subscribers
create table if not exists subscribers(
    id BIGSERIAL PRIMARY KEY,
    msisdn VARCHAR(200)
)

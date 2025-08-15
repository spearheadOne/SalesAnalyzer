-- liquibase formatted sql
-- changeset 002

SELECT create_hypertable('sales_agg', 'bucket_start', if_not_exists => TRUE);
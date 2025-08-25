-- liquibase formatted sql
-- changeset abondar:002

SELECT create_hypertable('sales_agg', 'bucket_start_time', if_not_exists => TRUE);
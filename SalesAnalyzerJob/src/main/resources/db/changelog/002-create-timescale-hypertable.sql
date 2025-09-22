-- liquibase formatted sql
-- changeset abondar:002

SELECT create_hypertable('sales_agg', 'event_time', if_not_exists => TRUE);

-- rollback SELECT drop_hypertable('sales_agg', if_exists => TRUE);
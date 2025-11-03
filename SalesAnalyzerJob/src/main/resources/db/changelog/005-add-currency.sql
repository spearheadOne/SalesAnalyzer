-- liquibase formatted sql
-- changeset abondar:005

ALTER TABLE sales_agg ADD COLUMN currency VARCHAR

-- rollback ALTER TABLE sales_agg DROP COLUMN IF EXISTS currency;
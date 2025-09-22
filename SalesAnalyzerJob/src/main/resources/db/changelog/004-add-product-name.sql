-- liquibase formatted sql
-- changeset abondar:004

ALTER TABLE sales_agg ADD COLUMN product_name VARCHAR(255);

-- rollback ALTER TABLE sales_agg DROP COLUMN IF EXISTS product_name;
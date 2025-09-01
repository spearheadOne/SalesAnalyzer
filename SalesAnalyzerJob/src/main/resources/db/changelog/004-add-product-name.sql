-- liquibase formatted sql
-- changeset abondar:004

ALTER TABLE sales_agg ADD COLUMN product_name VARCHAR(255);
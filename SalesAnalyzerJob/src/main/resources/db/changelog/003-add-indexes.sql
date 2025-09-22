-- liquibase formatted sql
-- changeset abondar:003

CREATE INDEX IF NOT EXISTS idx_sales_agg_time ON sales_agg (event_time);
CREATE INDEX IF NOT EXISTS idx_sales_agg_prod_time ON sales_agg (product_id, event_time);

-- rollback DROP INDEX IF EXISTS idx_sales_agg_time;
-- rollback DROP INDEX IF EXISTS idx_sales_agg_prod_time;
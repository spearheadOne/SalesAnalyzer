-- liquibase formatted sql
-- changeset abondar:001

CREATE TABLE IF NOT EXISTS sales_agg (
                           event_time TIMESTAMPTZ NOT NULL,
                           product_id   VARCHAR(255) NOT NULL,
                           category     VARCHAR(255) NOT NULL,
                           orders       BIGINT NOT NULL,
                           units        BIGINT NOT NULL,
                           revenue      DOUBLE PRECISION NOT NULL,
                           CONSTRAINT pk_sales_agg PRIMARY KEY (event_time, product_id)
);

-- rollback DROP TABLE IF EXISTS sales_agg;
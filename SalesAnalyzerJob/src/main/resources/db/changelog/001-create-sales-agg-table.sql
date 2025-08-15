-- liquibase formatted sql
-- changeset 001

CREATE TABLE IF NOT EXISTS sales_agg (
                           bucket_start TIMESTAMP NOT NULL,
                           product_id   TEXT NOT NULL,
                           category     TEXT NOT NULL,
                           orders       BIGINT NOT NULL,
                           units        BIGINT NOT NULL,
                           revenue      DOUBLE PRECISION NOT NULL,
                           CONSTRAINT pk_sales_agg PRIMARY KEY (bucket_start, product_id)
);
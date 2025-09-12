CREATE TABLE IF NOT EXISTS sales_agg (
                                         bucket_start_time TIMESTAMPTZ NOT NULL,
                                         product_id   TEXT NOT NULL,
                                         product_name VARCHAR(255),
                                         category     TEXT NOT NULL,
                                         orders       BIGINT NOT NULL,
                                         units        BIGINT NOT NULL,
                                         revenue      DOUBLE PRECISION NOT NULL,
                                         CONSTRAINT pk_sales_agg PRIMARY KEY (bucket_start_time, product_id)
);
CREATE TABLE IF NOT EXISTS sales_agg (
                                         event_time TIMESTAMPTZ NOT NULL,
                                         product_id   VARCHAR NOT NULL,
                                         product_name VARCHAR NOT NULL ,
                                         category     VARCHAR NOT NULL,
                                         orders       BIGINT NOT NULL,
                                         units        BIGINT NOT NULL,
                                         revenue      DOUBLE PRECISION NOT NULL,
                                         currency     VARCHAR NOT NULL,
                                         CONSTRAINT pk_sales_agg PRIMARY KEY (event_time, product_id)
);
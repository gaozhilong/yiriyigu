-- Table: public.t_stock

-- DROP TABLE public.t_stock;

CREATE TABLE public.t_stock
(
    id bigint NOT NULL DEFAULT nextval('t_stock_id_seq'::regclass),
    day text COLLATE pg_catalog."default" NOT NULL,
    code text COLLATE pg_catalog."default" NOT NULL,
    name text COLLATE pg_catalog."default" NOT NULL,
    tclose numeric,
    high numeric,
    low numeric,
    topen numeric,
    lclose numeric,
    chg numeric,
    pchg numeric,
    turnover numeric,
    voturnover numeric,
    vaturnover numeric,
    CONSTRAINT t_stock_pkey PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.t_stock
    OWNER to postgres;
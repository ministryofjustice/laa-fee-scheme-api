DROP TABLE IF EXISTS fee CASCADE;

DROP TABLE IF EXISTS fee_schemes CASCADE;

DROP TABLE IF EXISTS categories CASCADE;

DROP TABLE IF EXISTS category_bolt_on_fees CASCADE;

DROP TABLE IF EXISTS legal_help_categories CASCADE;

DROP TABLE IF EXISTS vat_rates CASCADE;

DROP TABLE IF EXISTS police_station_fees CASCADE;

-- above drop sqls need to be revisited once master data is inserted

CREATE TABLE IF NOT EXISTS fee_schemes
(
    scheme_code VARCHAR PRIMARY KEY,
    scheme_name VARCHAR NOT NULL,
    valid_from  DATE    NOT NULL,
    valid_to    DATE    NULL
);


CREATE TABLE IF NOT EXISTS fee
(
    fee_id                     SERIAL PRIMARY KEY,
    fee_code                   VARCHAR        NOT NULL,
    fee_scheme_code            VARCHAR        NOT NULL REFERENCES fee_schemes (scheme_code),
    total_fee                  NUMERIC(10, 2) NULL,
    profit_cost_limit          NUMERIC(10, 2) NULL,
    disbursement_limit         NUMERIC(10, 2) NULL,
    escape_threshold_limit     NUMERIC(10, 2) NULL,
    prior_authority_applicable BOOL           NULL,
    schedule_reference         BOOL           NULL
);

CREATE TABLE IF NOT EXISTS categories
(
    category_code VARCHAR PRIMARY KEY,
    category_name VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS category_bolt_on_fees
(
    category_bolt_on_fees_id        SERIAL PRIMARY KEY,
    category_code                   VARCHAR        NOT NULL REFERENCES categories (category_code),
    ho_interview_bolt_on            NUMERIC(10, 2) NULL,
    oral_cmrh_bolt_on               NUMERIC(10, 2) NULL,
    telephone_cmrh_bolt_on          NUMERIC(10, 2) NULL,
    substantive_heariitg_bolt_on    NUMERIC(10, 2) NULL,
    adjorn_hearing_bolt_on          NUMERIC(10, 2) NULL,
    region                          VARCHAR        NOT NULL
);

CREATE TABLE IF NOT EXISTS legal_help_categories
(
    legal_help_categories_id SERIAL PRIMARY KEY,
    category_code            VARCHAR      NOT NULL REFERENCES categories (category_code),
    full_description         VARCHAR(255) NOT NULL,
    area_of_law              VARCHAR(50)  DEFAULT 'Legal Help (Civil)'
);


CREATE TABLE IF NOT EXISTS vat_rates
(
    vat_rate_id     SERIAL PRIMARY KEY,
    rate_percentage numeric(7, 2) NOT NULL,
    valid_from      date          NOT NULL,
    valid_to        date          NULL
);


CREATE TABLE IF NOT EXISTS police_station_fees
(
    police_station_fees_id SERIAL PRIMARY KEY,
    criminal_justice_area  VARCHAR(255)  NOT NULL,
    police_station_name    VARCHAR(255)  NOT NULL,
    police_station_code    VARCHAR(255)  NOT NULL,
    fixed_fee              NUMERIC(10, 2) NULL,
    escape_threshold       NUMERIC(10, 2) NULL
);

DROP TABLE IF EXISTS fee CASCADE;

DROP TABLE IF EXISTS fee_schemes CASCADE;

DROP TABLE IF EXISTS category_of_law_look_up CASCADE;

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
    fee_id                      SERIAL PRIMARY KEY,
    fee_code                    VARCHAR        NOT NULL,
    fee_scheme_code             VARCHAR        NOT NULL REFERENCES fee_schemes (scheme_code),
    total_fee                   NUMERIC(10, 2) NULL,
    profit_cost_limit           NUMERIC(10, 2) NULL,
    disbursement_limit          NUMERIC(10, 2) NULL,
    escape_threshold_limit      NUMERIC(10, 2) NULL,
    prior_authority_applicable  BOOL           NULL,
    schedule_reference          BOOL           NULL,
    ho_interview_bolt_on        NUMERIC(10, 2) NULL,
    oral_cmrh_bolt_on           NUMERIC(10, 2) NULL,
    telephone_cmrh_bolt_on      NUMERIC(10, 2) NULL,
    substantive_hearing_bolt_on NUMERIC(10, 2) NULL,
    adjorn_hearing_bolt_on      NUMERIC(10, 2) NULL,
    region                      VARCHAR        NOT NULL
);

CREATE TABLE IF NOT EXISTS category_of_law_look_up
(
    category_of_law_look_up_id SERIAL PRIMARY KEY,
    category_code            VARCHAR      NOT NULL,
    full_description         VARCHAR(255) NOT NULL,
    area_of_law              VARCHAR(50)  NOT NULL,
    fee_code                 VARCHAR      NOT NULL
);

CREATE TABLE IF NOT EXISTS police_station_fees
(
    police_station_fees_id SERIAL PRIMARY KEY,
    criminal_justice_area  VARCHAR(255)   NOT NULL,
    police_station_name    VARCHAR(255)   NOT NULL,
    police_station_code    VARCHAR(255)   NOT NULL,
    fixed_fee              NUMERIC(10, 2) NULL,
    escape_threshold       NUMERIC(10, 2) NULL,
    fee_scheme_code        VARCHAR        NOT NULL REFERENCES fee_schemes (scheme_code)
);
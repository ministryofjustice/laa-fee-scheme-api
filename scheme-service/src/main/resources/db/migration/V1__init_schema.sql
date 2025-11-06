DROP TABLE IF EXISTS fee CASCADE;

DROP TABLE IF EXISTS fee_schemes CASCADE;

DROP TABLE IF EXISTS police_station_fees CASCADE;

DROP TABLE IF EXISTS fee_category_mapping CASCADE;

DROP TABLE IF EXISTS category_of_law_type CASCADE;

DROP TABLE IF EXISTS fee_scheme_category_type CASCADE;

DROP TABLE IF EXISTS area_of_law_type CASCADE;

DROP TABLE IF EXISTS police_stations CASCADE;

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
    description                 VARCHAR        NOT NULL,
    fee_scheme_code             VARCHAR        NOT NULL REFERENCES fee_schemes (scheme_code),
    category_type               VARCHAR        NULL,
    fee_type                    VARCHAR        NULL,
    region                      VARCHAR        NULL,
    fixed_fee                   NUMERIC(10, 2) NULL,
    profit_cost_limit           NUMERIC(10, 2) NULL,
    upper_cost_limit            NUMERIC(10, 2) NULL,
    disbursement_limit          NUMERIC(10, 2) NULL,
    escape_threshold_limit      NUMERIC(10, 2) NULL,
    total_limit                 NUMERIC(10, 2) NULL,
    prior_authority_applicable  BOOL           NULL,
    schedule_reference          BOOL           NULL,
    ho_interview_bolt_on        NUMERIC(10, 2) NULL,
    oral_cmrh_bolt_on           NUMERIC(10, 2) NULL,
    telephone_cmrh_bolt_on      NUMERIC(10, 2) NULL,
    substantive_hearing_bolt_on NUMERIC(10, 2) NULL,
    adjorn_hearing_bolt_on      NUMERIC(10, 2) NULL,
    mediation_fee_lower         NUMERIC(10, 2) NULL,
    mediation_fee_higher        NUMERIC(10, 2) NULL,
    court_designation_type      VARCHAR(50)    NULL,
    fee_band_type               VARCHAR        NULL
);


CREATE TABLE IF NOT EXISTS category_of_law_look_up
(
    category_of_law_look_up_id SERIAL PRIMARY KEY,
    category_code              VARCHAR      NOT NULL,
    full_description           VARCHAR(255) NOT NULL,
    area_of_law                VARCHAR(50)  NOT NULL,
    fee_code                   VARCHAR      NOT NULL
);


CREATE TABLE IF NOT EXISTS police_station_fees
(
    police_station_fees_id SERIAL PRIMARY KEY,
    criminal_justice_area  VARCHAR(255)   NOT NULL,
    ps_scheme_name         VARCHAR(255)   NOT NULL,
    ps_scheme_id           VARCHAR(255)   NOT NULL,
    fixed_fee              NUMERIC(10, 2) NULL,
    escape_threshold       NUMERIC(10, 2) NULL,
    fee_scheme_code        VARCHAR        NOT NULL REFERENCES fee_schemes (scheme_code)
);

CREATE TABLE IF NOT EXISTS police_stations
(
    id                  SERIAL PRIMARY KEY,
    police_station_id   varchar NOT NULL UNIQUE,
    police_station_name varchar NOT NULL,
    ps_scheme_id        varchar NOT NULL,
    ps_scheme_name      varchar NOT NULL
);

CREATE TABLE IF NOT EXISTS area_of_law_type
(
    area_of_law_type_id SERIAL PRIMARY KEY,
    code                VARCHAR(15) NOT NULL UNIQUE,
    description         VARCHAR(50) NOT NULL,
    case_type           VARCHAR     NOT NULL
);

CREATE TABLE IF NOT EXISTS category_of_law_type
(
    category_of_law_type_id SERIAL PRIMARY KEY,
    code                    VARCHAR(10) NOT NULL UNIQUE,
    description             TEXT        NOT null,
    area_of_law_type_id     INT         NOT NULL REFERENCES area_of_law_type (area_of_law_type_id)
);


CREATE TABLE IF NOT EXISTS fee_scheme_category_type
(
    fee_scheme_category_type_id SERIAL PRIMARY KEY,
    fee_scheme_category_name    VARCHAR(50) NOT NULL UNIQUE
);


CREATE TABLE IF NOT EXISTS fee_category_mapping
(
    id                          SERIAL PRIMARY KEY,
    fee_code                    VARCHAR(10) NOT NULL UNIQUE,
    fee_description             TEXT        NOT NULL,
    fee_type                    VARCHAR(15) NOT NULL,
    fee_scheme_category_type_id INT         NOT NULL REFERENCES fee_scheme_category_type (fee_scheme_category_type_id),
    category_of_law_type_id     INT         NOT NULL REFERENCES category_of_law_type (category_of_law_type_id)
);


ALTER TABLE category_of_law_look_up
    ADD CONSTRAINT uq_category_of_law_look_up_category_code_fee_code UNIQUE (category_code, fee_code);

ALTER TABLE police_station_fees
    ADD CONSTRAINT uq_police_station_fees_category_code_fee_code UNIQUE (ps_scheme_id, fee_scheme_code);

ALTER TABLE fee
    ADD CONSTRAINT uq_fee_scheme_code_fee_code UNIQUE (fee_code, fee_scheme_code);

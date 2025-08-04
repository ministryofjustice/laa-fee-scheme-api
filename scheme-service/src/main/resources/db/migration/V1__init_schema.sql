DROP TABLE IF EXISTS fee CASCADE;

DROP TABLE IF EXISTS fee_schemes CASCADE;

DROP TABLE IF EXISTS category_of_law_look_up CASCADE;

DROP TABLE IF EXISTS police_station_fees CASCADE;
-- above drop sqls need to be revisited once master data is inserted

CREATE TABLE IF NOT EXISTS fee_schemes
(
    scheme_code varchar PRIMARY KEY,
    scheme_name varchar NOT NULL,
    valid_from  date    NOT NULL,
    valid_to    date    NULL
);

CREATE TABLE IF NOT EXISTS fee
(
    fee_id                      SERIAL PRIMARY KEY,
    fee_code                    varchar        NOT NULL,
    fee_scheme_code             varchar        NOT NULL REFERENCES fee_schemes (scheme_code),
    total_fee                   numeric(10, 2) NULL,
    profit_cost_limit           numeric(10, 2) NULL,
    disbursement_limit          numeric(10, 2) NULL,
    escape_threshold_limit      numeric(10, 2) NULL,
    prior_authority_applicable  bool           NULL,
    schedule_reference          bool           NULL,
    ho_interview_bolt_on        numeric(10, 2) NULL,
    oral_cmrh_bolt_on           numeric(10, 2) NULL,
    telephone_cmrh_bolt_on      numeric(10, 2) NULL,
    substantive_hearing_bolt_on numeric(10, 2) NULL,
    adjorn_hearing_bolt_on      numeric(10, 2) NULL,
    mediation_session_one       numeric(10, 2) NULL,
    mediation_session_two       numeric(10, 2) NULL,
    region                      varchar        NOT NULL
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
    criminal_justice_area  varchar(255)   NOT NULL,
    police_station_name    varchar(255)   NOT NULL,
    police_station_code    varchar(255)   NOT NULL,
    fixed_fee              numeric(10, 2) NULL,
    escape_threshold       numeric(10, 2) NULL,
    fee_scheme_code        varchar        NOT NULL REFERENCES fee_schemes (scheme_code)
);

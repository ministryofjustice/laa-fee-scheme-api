DROP TABLE IF EXISTS fee CASCADE;

DROP TABLE IF EXISTS fee_schemes CASCADE;

DROP TABLE IF EXISTS categories CASCADE;

DROP TABLE IF EXISTS category_bolt_on_fees CASCADE;

DROP TABLE IF EXISTS legal_help_categories CASCADE;

CREATE TABLE IF NOT EXISTS fee_schemes
(
    scheme_code TEXT PRIMARY KEY,
    scheme_name varchar NOT NULL,
    valid_from  date    NOT NULL,
    valid_to    date    NULL
);


CREATE TABLE IF NOT EXISTS fee
(
    fee_id                     SERIAL PRIMARY KEY,
    fee_code                   varchar UNIQUE NOT NULL,
    fee_scheme_code            TEXT           NOT NULL REFERENCES fee_schemes (scheme_code),
    total_fee                  numeric        NULL,
    profit_cost_limit          numeric        NULL,
    disbursement_limit         numeric        NULL,
    prior_authority_applicable bool           NULL,
    schedule_reference         bool           NULL
);

CREATE TABLE IF NOT EXISTS categories
(
    category_code TEXT PRIMARY KEY,
    category_name varchar NOT NULL
);

CREATE TABLE IF NOT EXISTS category_bolt_on_fees
(
    id                          SERIAL PRIMARY KEY,
    category_code               TEXT    NOT NULL REFERENCES categories (category_code),
    ho_interview_bolt_on        numeric NULL,
    oral_cmrh_bolt_on           numeric NULL,
    telephone_cmrh_bolt_on      numeric NULL,
    substantive_hearing_bolt_on numeric NULL,
    adjorn_hearing_bolt_on      numeric NULL
);

CREATE TABLE IF NOT EXISTS legal_help_categories
(
    id               SERIAL PRIMARY KEY,
    category_code    TEXT         NOT NULL REFERENCES categories (category_code),
    full_description VARCHAR(255) NOT NULL,
    area_of_law      VARCHAR(50) DEFAULT 'Legal Help (Civil)'
);


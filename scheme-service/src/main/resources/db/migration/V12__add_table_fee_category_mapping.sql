DROP TABLE IF EXISTS fee_category_mapping CASCADE;

DROP TABLE IF EXISTS category_of_law_type CASCADE;

DROP TABLE IF EXISTS fee_scheme_category_type CASCADE;

DROP TABLE IF EXISTS area_of_law_type CASCADE;

DROP TABLE IF EXISTS category_of_law_look_up CASCADE;


CREATE TABLE IF NOT EXISTS area_of_law_type
(
    area_of_law_type_id SERIAL PRIMARY KEY,
    code                VARCHAR(15) NOT NULL UNIQUE,
    description         VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS category_of_law_type
(
    category_of_law_type_id SERIAL PRIMARY KEY,
    code                    VARCHAR(10) NOT NULL UNIQUE,
    description             TEXT        NOT null,
    area_of_law_type_id     int         NOT NULL REFERENCES area_of_law_type (area_of_law_type_id)
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
    fee_scheme_category_type_id int         NOT NULL REFERENCES fee_scheme_category_type (fee_scheme_category_type_id),
    category_of_law_type_id     int         NOT NULL REFERENCES category_of_law_type (category_of_law_type_id)
);

DROP TABLE IF EXISTS fee_code_groupings CASCADE;

DROP TABLE IF EXISTS fee_compatability_mapping CASCADE;

CREATE TABLE fee_code_groupings (
    code VARCHAR(50) PRIMARY KEY,
    description VARCHAR(255)
);

CREATE TABLE fee_compatibility_mapping (
   id SERIAL PRIMARY KEY,
   source_grouping_code VARCHAR(100) REFERENCES fee_code_groupings(code),
   target_grouping_code VARCHAR(100) REFERENCES fee_code_groupings(code),
   compatible BOOLEAN NOT NULL,
   CHECK (
       (
            (source_grouping_code is not null)::integer
       ) = 1
    ),
    CHECK (
        (
            (target_grouping_code is not null)::integer
        ) = 1
    ),
    UNIQUE (source_grouping_code, target_grouping_code)
);

ALTER TABLE fee_code_information
    ADD COLUMN grouping_code VARCHAR(50) REFERENCES fee_code_groupings(code);
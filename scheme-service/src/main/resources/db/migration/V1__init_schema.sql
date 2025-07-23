
CREATE TABLE IF NOT EXISTS fee_schemes (
                                           fee_schemes_id  SERIAL PRIMARY KEY,
                                           fee_scheme_code varchar UNIQUE NOT NULL,
                                           "name"          varchar        NOT NULL,
                                           valid_from      date           NOT NULL,
                                           valid_to        date           NULL
);


CREATE TABLE IF NOT EXISTS fee (
                                   fee_id                      SERIAL PRIMARY KEY,
                                   fee_code                    varchar UNIQUE NOT NULL,
                                   fee_schemes_id              int8           NOT NULL,
                                   total_fee                   numeric        NULL,
                                   profit_cost_limit           numeric        NULL,
                                   disbursement_limit          numeric        NULL,
                                   prior_authority_applicable  bool           NULL,
                                   ho_interview_bolt_on        varchar        NULL,
                                   oral_cmrh_bolt_on           varchar        NULL,
                                   telephone_cmrh_bolt_on      varchar        NULL,
                                   substantive_hearing_bolt_on varchar        NULL,
                                   adjorn_hearing_bolt_on      varchar        NULL,
                                   schedule_reference          varchar        NULL
);


ALTER TABLE fee ADD CONSTRAINT fee_fee_schemes_fk FOREIGN KEY (fee_schemes_id) REFERENCES fee_schemes(fee_schemes_id);

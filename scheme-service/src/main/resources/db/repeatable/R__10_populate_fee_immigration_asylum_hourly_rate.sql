INSERT INTO fee
(fee_code, description, profit_cost_limit, disbursement_limit, total_limit, fee_scheme_code, category_type, fee_type)
VALUES ('IAXL', 'LH Hourly Rates - Asylum', 800.00, 400.00, NULL, 'IMM_ASYLM_FS2013', 'IMMIGRATION_ASYLUM', 'HOURLY'),
       ('IMXL', 'LH Hourly Rates - Imm', 500.00, 400.00, NULL, 'IMM_ASYLM_FS2013', 'IMMIGRATION_ASYLUM', 'HOURLY'),
       ('IA100', 'LH Hourly Rates - £100 total limit', NULL, NULL, 100.00, 'IMM_ASYLM_FS2013', 'IMMIGRATION_ASYLUM', 'HOURLY'),
       ('IAXC', 'CLR Hourly Rates Asylum', NULL, NULL, 1600.00, 'IMM_ASYLM_FS2013', 'IMMIGRATION_ASYLUM', 'HOURLY'),
       ('IMXC', 'CLR Hourly Rates Imm', NULL, NULL, 1200.00, 'IMM_ASYLM_FS2013', 'IMMIGRATION_ASYLUM', 'HOURLY'),
       ('IRAR', 'CLR Upper Tribunal Transitional cases', NULL, NULL, NULL, 'IMM_ASYLM_FS2013', 'IMMIGRATION_ASYLUM', 'HOURLY')
ON CONFLICT (fee_code, fee_scheme_code) DO UPDATE
SET
    category_type = EXCLUDED.category_type,
    fee_type = EXCLUDED.fee_type;

INSERT INTO fee
(fee_code, description, profit_cost_limit, disbursement_limit, total_limit, adjorn_hearing_bolt_on, oral_cmrh_bolt_on, telephone_cmrh_bolt_on, substantive_hearing_bolt_on, fee_scheme_code, category_type, fee_type)
VALUES ('IACD', 'Interim hourly rates - Asylum CLR', NULL, NULL, 1600.00, 161.00, 166.00, 90.00, 302.00, 'IMM_ASYLM_FS2020', 'IMMIGRATION_ASYLUM', 'HOURLY'),
       ('IMCD', 'Interim hourly rates - Immigration Interim CLR', NULL, NULL, 1200.00, 161.00, 166.00, 90.00, 237.00, 'IMM_ASYLM_FS2020', 'IMMIGRATION_ASYLUM', 'HOURLY')
ON CONFLICT (fee_code, fee_scheme_code) DO UPDATE
SET
    category_type = EXCLUDED.category_type,
    fee_type = EXCLUDED.fee_type;


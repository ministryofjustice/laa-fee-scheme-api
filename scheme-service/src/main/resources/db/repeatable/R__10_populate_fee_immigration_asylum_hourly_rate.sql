INSERT INTO fee
(fee_code, description, profit_cost_limit, disbursement_limit, total_limit, fee_scheme_code, calculation_type, fee_type)
VALUES ('IAXL', 'LH Hourly Rates - Asylum', 800.00, 400.00, NULL, 'I&A_FS2013', 'IMMIGRATION_ASYLUM_HOURLY_RATE', 'HOURLY'),
       ('IMXL', 'LH Hourly Rates - Imm', 500.00, 400.00, NULL, 'I&A_FS2013', 'IMMIGRATION_ASYLUM_HOURLY_RATE', 'HOURLY'),
       ('IA100', 'LH Hourly Rates - £100 total limit', NULL, NULL, 100.00, 'I&A_FS2013', 'IMMIGRATION_ASYLUM_HOURLY_RATE', 'HOURLY'),
       ('IAXC', 'CLR Hourly Rates Asylum', NULL, NULL, 1600.00, 'I&A_FS2013', 'IMMIGRATION_ASYLUM_HOURLY_RATE', 'HOURLY'),
       ('IMXC', 'CLR Hourly Rates Imm', NULL, NULL, 1200.00, 'I&A_FS2013', 'IMMIGRATION_ASYLUM_HOURLY_RATE', 'HOURLY'),
       ('IRAR', 'CLR Upper Tribunal Transitional cases', NULL, NULL, NULL, 'I&A_FS2013', 'IMMIGRATION_ASYLUM_HOURLY_RATE', 'HOURLY')
ON CONFLICT (fee_code, fee_scheme_code) DO UPDATE
SET
    fee_type = EXCLUDED.fee_type;

INSERT INTO fee
(fee_code, description, profit_cost_limit, disbursement_limit, total_limit, adjorn_hearing_bolt_on, oral_cmrh_bolt_on, telephone_cmrh_bolt_on, fee_scheme_code, calculation_type, fee_type)
VALUES ('IACD', 'Interim hourly rates - Asylum CLR', NULL, NULL, 1600.00, NULL, NULL, NULL,'I&A_FS2013', 'IMMIGRATION_ASYLUM_HOURLY_RATE', 'HOURLY'),
       ('IACD', 'Interim hourly rates - Asylum CLR', NULL, NULL, 1600.00, 161.00, 166.00, 90.00, 'I&A_FS2020', 'IMMIGRATION_ASYLUM_HOURLY_RATE', 'HOURLY'),
       ('IACD', 'Interim hourly rates - Asylum CLR', NULL, NULL, 1600.00, NULL, NULL, NULL, 'I&A_FS2023', 'IMMIGRATION_ASYLUM_HOURLY_RATE', 'HOURLY'),
       ('IMCD', 'Interim hourly rates - Immigration Interim CLR', NULL, NULL, 1200.00, NULL, NULL, NULL, 'I&A_FS2013', 'IMMIGRATION_ASYLUM_HOURLY_RATE', 'HOURLY'),
       ('IMCD', 'Interim hourly rates - Immigration Interim CLR', NULL, NULL, 1200.00, 161.00, 166.00, 90.00, 'I&A_FS2020', 'IMMIGRATION_ASYLUM_HOURLY_RATE', 'HOURLY'),
       ('IMCD', 'Interim hourly rates - Immigration Interim CLR', NULL, NULL, 1200.00, NULL, NULL, NULL, 'I&A_FS2023', 'IMMIGRATION_ASYLUM_HOURLY_RATE', 'HOURLY')
ON CONFLICT (fee_code, fee_scheme_code) DO UPDATE
SET
    fee_type = EXCLUDED.fee_type;


INSERT INTO fee
(fee_code, profit_cost_limit, disbursement_limit, total_limit, fee_scheme_code)
VALUES ('IAXL', 800.00, 400.00, NULL, 'IMM_ASYLM_FS2013'),
       ('IMXL', 500.00, 400.00, NULL, 'IMM_ASYLM_FS2013'),
       ('IA100', NULL, NULL, 100.00, 'IMM_ASYLM_FS2013'),
       ('IAXC', NULL, NULL, 1600.00, 'IMM_ASYLM_FS2013'),
       ('IMXC', NULL, NULL, 1200.00, 'IMM_ASYLM_FS2013'),
       ('IRAR', NULL, NULL, NULL, 'IMM_ASYLM_FS2013')
ON CONFLICT (fee_code, fee_scheme_code) DO NOTHING;

INSERT INTO fee
(fee_code, profit_cost_limit, disbursement_limit, total_limit, adjorn_hearing_bolt_on, oral_cmrh_bolt_on, telephone_cmrh_bolt_on, substantive_hearing_bolt_on, fee_scheme_code)
VALUES ('IACD', NULL, NULL, 1600.00, 161.00, 166.00, 90.00, 302.00, 'IMM_ASYLM_FS2020'),
       ('IMCD', NULL, NULL, 1200.00, 161.00, 166.00, 90.00, 237.00, 'IMM_ASYLM_FS2020')
ON CONFLICT (fee_code, fee_scheme_code) DO NOTHING;


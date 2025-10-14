INSERT INTO fee (fee_code, description, disbursement_limit, fee_scheme_code, fee_type)
VALUES ('ICASD', 'Asylum CLR Hourly Rates Stage Disbursement', 1600.00, 'IMM_ASYLM_FS2013', 'DISBURSEMENT_ONLY'),
       ('ICISD', 'Immigration CLR Hourly Rates Stage Disbursement', 1200.00, 'IMM_ASYLM_FS2013', 'DISBURSEMENT_ONLY'),
       ('ICSSD', 'CLR SFS Stage Disbursement', 600.00, 'IMM_ASYLM_FS2013', 'DISBURSEMENT_ONLY'),
       ('ILHSD', 'LH Stage Disbursement', 400.00, 'IMM_ASYLM_FS2013', 'DISBURSEMENT_ONLY')
ON CONFLICT (fee_code, fee_scheme_code) DO NOTHING
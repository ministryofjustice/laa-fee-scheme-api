INSERT INTO fee (fee_code, description, disbursement_limit, fee_scheme_code, category_type, fee_type)
VALUES ('ICASD', 'Asylum CLR Hourly Rates Stage Disbursement', 1600.00, 'IMM_ASYLM_DISBURSEMENT_FS2020', 'IMMIGRATION_ASYLUM', 'DISB_ONLY'),
       ('ICISD', 'Immigration CLR Hourly Rates Stage Disbursement', 1200.00, 'IMM_ASYLM_DISBURSEMENT_FS2020', 'IMMIGRATION_ASYLUM', 'DISB_ONLY'),
       ('ICSSD', 'CLR SFS Stage Disbursement', 600.00, 'IMM_ASYLM_DISBURSEMENT_FS2020', 'IMMIGRATION_ASYLUM', 'DISB_ONLY'),
       ('ILHSD', 'LH Stage Disbursement', 400.00, 'IMM_ASYLM_DISBURSEMENT_FS2020', 'IMMIGRATION_ASYLUM', 'DISB_ONLY')
    ON CONFLICT (fee_code, fee_scheme_code) DO NOTHING;

DELETE FROM fee WHERE fee_scheme_code ='IMM_ASYLM_DISBURSEMENT_FS2013';
DELETE FROM fee_schemes WHERE scheme_code ='IMM_ASYLM_DISBURSEMENT_FS2013';

-- Fee Scheme for 2016 'Advocacy Assistance in the Crown Court or Appeals & Reviews' category

DELETE FROM fee WHERE fee_type  ='IMM_ASYLM_DISBURSEMENT_FS2013' AND category_type = 'ADVOCACY_APPEALS_REVIEWS';

INSERT INTO fee
(fee_code, description, total_limit, category_type, fee_type, fee_scheme_code)
VALUES
    ('PROH', 'Advocacy Assistance in the Crown Court', 1368.75, 'ADVOCACY_APPEALS_REVIEWS', 'HOURLY', 'AAR_FS2016'),
    ('APPA', 'Advice and assistance in relation to an appeal (except CCRC)', 273.75, 'ADVOCACY_APPEALS_REVIEWS', 'HOURLY', 'AAR_FS2016'),
    ('APPB', 'Advice and assistance in relation to CCRC application', 456.25, 'ADVOCACY_APPEALS_REVIEWS', 'HOURLY', 'AAR_FS2016')
ON CONFLICT (fee_code, fee_scheme_code) DO NOTHING;

-- Fee Scheme for 2022 'Advocacy Assistance in the Crown Court or Appeals & Reviews' category
INSERT INTO fee
(fee_code, description, total_limit, category_type, fee_type, fee_scheme_code)
VALUES
    ('PROH', 'Advocacy Assistance in the Crown Court', 1574.06, 'ADVOCACY_APPEALS_REVIEWS', 'HOURLY', 'AAR_FS2022'),
    ('APPA', 'Advice and assistance in relation to an appeal (except CCRC)', 314.81, 'ADVOCACY_APPEALS_REVIEWS', 'HOURLY', 'AAR_FS2022'),
    ('APPB', 'Advice and assistance in relation to CCRC application', 524.69, 'ADVOCACY_APPEALS_REVIEWS', 'HOURLY', 'AAR_FS2022')
ON CONFLICT (fee_code, fee_scheme_code) DO NOTHING;
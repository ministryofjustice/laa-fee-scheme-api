-- Temp delete statements to remove old 'Miscellaneous' fee scheme codes
DELETE FROM fee WHERE fee_scheme_code in ('MISCCON_FS2013', 'MISCEMP_FS2013', 'MISCGEN_FS2013', 'MISCPI_FS2013', 'MISCASBI_FS2015');
DELETE FROM fee_schemes WHERE scheme_code in ('MISCCON_FS2013', 'MISCEMP_FS2013', 'MISCGEN_FS2013', 'MISCPI_FS2013', 'MISCASBI_FS2015');


-- Fee Scheme for 'Claims Against Public Authorities' category
INSERT INTO fee_schemes (scheme_code, scheme_name, valid_from, valid_to)
VALUES
    ('CAPA_FS2013', 'Claims Against Public Authorities Fee Scheme 2013', '2013-04-01', NULL)
ON CONFLICT (scheme_code) DO NOTHING;

-- Fee Scheme for 'Clinical Negligence' category
INSERT INTO fee_schemes (scheme_code, scheme_name, valid_from, valid_to)
VALUES
    ('CLIN_FS2013', 'Clinical Negligence Fee Scheme 2013', '2013-04-01', NULL)
ON CONFLICT (scheme_code) DO NOTHING;

-- Fee Scheme for 'Community Care' category
INSERT INTO fee_schemes (scheme_code, scheme_name, valid_from, valid_to)
VALUES
    ('COM_FS2013', 'Community Care Fee Scheme 2013', '2013-04-01', NULL)
ON CONFLICT (scheme_code) DO NOTHING;

-- Fee Scheme for 'Debt' category
INSERT INTO fee_schemes (scheme_code, scheme_name, valid_from, valid_to)
VALUES
    ('DEBT_FS2013', 'Debt Fee Scheme 2013', '2013-04-01', NULL)
ON CONFLICT (scheme_code) DO NOTHING;

-- Fee Scheme for 'Discrimination' category
INSERT INTO fee_schemes (scheme_code, scheme_name, valid_from, valid_to)
VALUES
    ('DISC_FS2013', 'Discrimination Scheme 2013', '2013-04-01', NULL)
    ON CONFLICT (scheme_code) DO NOTHING;

-- Fee Scheme for 'Housing - HLPAS' category
INSERT INTO fee_schemes (scheme_code, scheme_name, valid_from, valid_to)
VALUES
    ('ELA_FS2024', 'Housing - HLPAS Fee Scheme 2024', '2024-09-01', NULL)
ON CONFLICT (scheme_code) DO NOTHING;

-- Fee Scheme for 'Housing' category
INSERT INTO fee_schemes (scheme_code, scheme_name, valid_from, valid_to)
VALUES
    ('HOUS_FS2013', 'Housing Fee Scheme 2013', '2013-04-01', NULL)
ON CONFLICT (scheme_code) DO NOTHING;

-- Fee Scheme for 'Immigration and Asylum' category
INSERT INTO fee_schemes (scheme_code, scheme_name, valid_from, valid_to)
VALUES
    ('IMM_ASYLM_FS2013', 'Immigration and Asylum Scheme 2013', '2013-04-01', NULL),
    ('IMM_ASYLM_FS2020', 'Immigration and Asylum Scheme 2020', '2020-06-08', NULL),
    ('IMM_ASYLM_FS2023', 'Immigration and Asylum Scheme 2023', '2023-04-01', NULL)
    ON CONFLICT (scheme_code) DO NOTHING;

-- Fee Scheme for 'Criminal proceedings - Magistrates court' category
INSERT INTO fee_schemes (scheme_code, scheme_name, valid_from, valid_to)
VALUES
    ('MAGS_COURT_FS2016', 'Criminal proceedings - Magistrates court 2016', '2016-04-01', '2022-09-29'),
    ('MAGS_COURT_FS2022', 'Criminal proceedings - Magistrates court 2022', '2022-09-30', NULL)
ON CONFLICT (scheme_code) DO NOTHING;

-- Fee Scheme for 'Criminal proceedings - Youth court' category
INSERT INTO fee_schemes (scheme_code, scheme_name, valid_from, valid_to)
VALUES
    ('YOUTH_COURT_FS2024', 'Criminal proceedings - Youth court 2022', '2024-12-06', NULL)
    ON CONFLICT (scheme_code) DO NOTHING;

-- Fee Scheme for 'Mediation' category
INSERT INTO fee_schemes (scheme_code, scheme_name, valid_from, valid_to)
VALUES
    ('MED_FS2013', 'Mediation Fee Scheme 2013', '2013-04-01', NULL)
ON CONFLICT (scheme_code) DO NOTHING;

-- Fee Scheme for 'Mental Health' category
INSERT INTO fee_schemes (scheme_code, scheme_name, valid_from, valid_to)
VALUES
    ('MHL_FS2013', 'Mental Health Fee Scheme 2013', '2013-04-01', NULL)
    ON CONFLICT (scheme_code) DO NOTHING;

-- Fee Scheme for 'Miscellaneous' category
INSERT INTO fee_schemes (scheme_code, scheme_name, valid_from, valid_to)
VALUES
    ('MISC_FS2013', 'Miscellaneous Fee Scheme 2013', '2013-04-01', NULL),
    ('MISC_FS2015', 'Miscellaneous Fee Scheme 2015', '2015-03-23', NULL)
ON CONFLICT (scheme_code) DO NOTHING;

-- Fee Scheme for 'Police Station' category
INSERT INTO fee_schemes (scheme_code, scheme_name, valid_from, valid_to)
VALUES ('POL_FS2016', 'Police Station Work 2016', '2016-04-01', '2022-09-29'),
       ('POL_FS2022', 'Police Station Work 2022', '2022-09-30', '2022-12-05'),
       ('POL_FS2024', 'Police Station Work 2024', '2024-12-06', NULL),
       ('TBD', 'Police Station Work -TBD', '2025-08-04', NULL),
       ('POL_FS2021', 'Police Station Work 2021', '2021-06-07', NULL)
ON CONFLICT (scheme_code) DO NOTHING;

-- Fee Scheme for 'Public Law' category
INSERT INTO fee_schemes (scheme_code, scheme_name, valid_from, valid_to)
VALUES
    ('PUB_FS2013', 'Public Law Fee Scheme 2013', '2013-04-01', NULL)
ON CONFLICT (scheme_code) DO NOTHING;


-- Fee Scheme for 'Education' category
INSERT INTO fee_schemes (scheme_code, scheme_name, valid_from, valid_to)
VALUES
    ('EDU_FS2013', 'Education Fee Scheme 2013', '2013-04-01', NULL)
ON CONFLICT (scheme_code) DO NOTHING;

-- Fee Scheme for 'Welfare Benefits' category
INSERT INTO fee_schemes (scheme_code, scheme_name, valid_from, valid_to)
VALUES
    ('WB_FS2023', 'Welfare Benefits Fee Scheme 2023', '2023-04-01', '2025-04-30'),
    ('WB_FS2025', 'Welfare Benefits Fee Scheme 2025', '2025-05-01', NULL)
ON CONFLICT (scheme_code) DO NOTHING;



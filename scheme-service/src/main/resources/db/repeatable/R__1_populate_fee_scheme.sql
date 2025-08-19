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

-- Fee Scheme for 'Mediation' category
INSERT INTO fee_schemes (scheme_code, scheme_name, valid_from, valid_to)
VALUES
    ('MED_FS2013', 'Mediation Fee Scheme 2013', '2013-04-01', NULL)
ON CONFLICT (scheme_code) DO NOTHING;

-- Fee Scheme for 'Miscellaneous' category
INSERT INTO fee_schemes (scheme_code, scheme_name, valid_from, valid_to)
VALUES
    ('MISCASBI_FS2015', 'Miscellaneous Fee Scheme 2015', '2015-03-23', NULL),
    ('MISCCON_FS2013', 'Miscellaneous Fee Scheme 2013', '2013-04-01', NULL),
    ('MISCEMP_FS2013', 'Miscellaneous Fee Scheme 2013', '2013-04-01', NULL),
    ('MISCGEN_FS2013', 'Miscellaneous Fee Scheme 2013', '2013-04-01', NULL),
    ('MISCPI_FS2013', 'Miscellaneous Fee Scheme 2013', '2013-04-01', NULL)
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



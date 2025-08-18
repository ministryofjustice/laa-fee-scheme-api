-- Fee Scheme for 'Police Station' category
INSERT INTO fee_schemes (scheme_code, scheme_name, valid_from, valid_to)
VALUES ('POL_FS2016', 'Police Station Work 2016', '2016-04-01', '2022-09-29'),
       ('POL_FS2022', 'Police Station Work 2022', '2022-09-30', '2022-12-05'),
       ('POL_FS2024', 'Police Station Work 2024', '2024-12-06', NULL),
       ('TBD', 'Police Station Work -TBD', '2025-08-04', NULL),
       ('POL_FS2021', 'Police Station Work 2021', '2021-06-07', NULL)
ON CONFLICT (scheme_code) DO NOTHING;

-- Fee Scheme for 'Mediation' category
INSERT INTO fee_schemes (scheme_code, scheme_name, valid_from, valid_to)
VALUES
    ('MED_FS2013', 'Mediation Fee Scheme 2013', '2013-04-01', NULL)
ON CONFLICT (scheme_code) DO NOTHING;

-- Fee Scheme for 'Other Civil Cases' category
INSERT INTO fee_schemes (scheme_code, scheme_name, valid_from, valid_to)
VALUES
    ('COM_FS2013', 'Community Care Fee Scheme 2013', '2013-04-01', NULL) -- awaiting scheme date
    ON CONFLICT (scheme_code) DO NOTHING;

-- Fee Scheme for 'Advice and Assistance and Advocacy Assistance by a court Duty Solicitor' category
INSERT INTO fee_schemes (scheme_code, scheme_name, valid_from, valid_to)
VALUES
    ('AAA_FS2016', 'Advocacy Assistance in the Crown Court or Appeals & Reviews Fee Scheme 2016', '2016-04-01', NULL)
ON CONFLICT (scheme_code) DO NOTHING;

-- Fee Scheme for 'Advocacy Assistance in the Crown Court or Appeals & Reviews' category
INSERT INTO fee_schemes (scheme_code, scheme_name, valid_from, valid_to)
VALUES
    ('AAR_FS2016', 'Advocacy Assistance in the Crown Court or Appeals & Reviews Fee Scheme 2016', '2016-04-01', '2022-09-29'),
    ('AAR_FS2022', 'Advocacy Assistance in the Crown Court or Appeals & Reviews Fee Scheme 2022', '2022-09-30', '2025-12-21'),
    ('AAR_FS2025', 'Advocacy Assistance in the Crown Court or Appeals & Reviews Fee Scheme 2025', '2025-12-22', NULL)
ON CONFLICT (scheme_code) DO NOTHING;

-- Fee Scheme for 'Associated Civil' category
INSERT INTO fee_schemes (scheme_code, scheme_name, valid_from, valid_to)
VALUES
    ('ASSOC_FS2016', 'Associated Civil Fee Scheme 2016', '2016-04-01', NULL)
ON CONFLICT (scheme_code) DO NOTHING;

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
    ('DEBT_FS2013', 'Debt Fee Scheme 2013', '2013-04-01', NULL),
    ('DEBT_FS2025', 'Debt Fee Scheme 2025', '2025-12-22', NULL)
ON CONFLICT (scheme_code) DO NOTHING;

-- Fee Scheme for 'Discrimination' category
INSERT INTO fee_schemes (scheme_code, scheme_name, valid_from, valid_to)
VALUES
    ('DISC_FS2013', 'Discrimination Scheme 2013', '2013-04-01', NULL)
ON CONFLICT (scheme_code) DO NOTHING;

-- Fee Scheme for 'Early Cover or Refused Means Test' category
INSERT INTO fee_schemes (scheme_code, scheme_name, valid_from, valid_to)
VALUES
    ('EC_RMT_FS2016', 'Early Cover or Refused Means Test Fee Scheme 2016', '2016-04-01', '2022-09-29'),
    ('EC_RMT_FS2022', 'Early Cover or Refused Means Test Fee Scheme 2022', '2022-09-30', '2025-12-21'),
    ('EC_RMT_FS2025', 'Early Cover or Refused Means Test Fee Scheme 2025', '2025-12-22', NULL)
ON CONFLICT (scheme_code) DO NOTHING;

-- Fee Scheme for 'Education' category
INSERT INTO fee_schemes (scheme_code, scheme_name, valid_from, valid_to)
VALUES
    ('EDU_DISB_FS2013', 'Education - Disbursement Fee Scheme 2013', '2013-04-01', NULL),
    ('EDU_FS2013', 'Education Fee Scheme 2013', '2013-04-01', NULL)
ON CONFLICT (scheme_code) DO NOTHING;

-- Fee Scheme for 'Housing - HLPAS' category
INSERT INTO fee_schemes (scheme_code, scheme_name, valid_from, valid_to)
VALUES
    ('ELA_FS2024', 'Housing - HLPAS Fee Scheme 2024', '2024-09-01', NULL),
    ('ELA_FS2025', 'Housing - HLPAS Fee Scheme 2025', '2025-12-22', NULL)
ON CONFLICT (scheme_code) DO NOTHING;

-- Fee Scheme for 'Family' category
INSERT INTO fee_schemes (scheme_code, scheme_name, valid_from, valid_to)
VALUES
    ('FAM_LON_FS2013', 'Family London Rate Fee Scheme 2013', '2013-04-01', NULL),
    ('FAM_NON_LON_FS2013', 'Family Non London Rate Fee Scheme 2013', '2013-04-01', NULL)
ON CONFLICT (scheme_code) DO NOTHING;

-- Fee Scheme for 'Housing' category
INSERT INTO fee_schemes (scheme_code, scheme_name, valid_from, valid_to)
VALUES
    ('HOUS_FS2013', 'Housing Fee Scheme 2013', '2013-04-01', NULL),
    ('HOUS_FS2025', 'Housing Fee Scheme 2025', '2025-12-22', NULL)
ON CONFLICT (scheme_code) DO NOTHING;

-- Fee Scheme for 'Immigration and Asylum' category
INSERT INTO fee_schemes (scheme_code, scheme_name, valid_from, valid_to)
VALUES
    ('IMM_ASYLM_DISBURSEMENT_FS2013', 'Immigration and Asylum Disbursement Scheme 2013', '2013-04-01', NULL),
    ('IMM_ASYLM_FS2013', 'Immigration and Asylum Scheme 2013', '2013-04-01', '2020-06-07'),
    ('IMM_ASYLM_FS2020', 'Immigration and Asylum Scheme 2020', '2020-06-08', '2023-03-31'),
    ('IMM_ASYLM_FS2023', 'Immigration and Asylum Scheme 2023', '2023-04-01', NULL),
    ('IMM_ASYLM_FS2025', 'Immigration and Asylum Scheme 2025', '2025-12-22', NULL)
ON CONFLICT (scheme_code) DO NOTHING;

-- Fee Scheme for 'Criminal proceedings - Magistrates court' category
INSERT INTO fee_schemes (scheme_code, scheme_name, valid_from, valid_to)
VALUES
    ('MAGS_COURT_FS2016', 'Criminal proceedings - Magistrates court 2016', '2016-04-01', '2022-09-29'),
    ('MAGS_COURT_FS2022', 'Criminal proceedings - Magistrates court 2022', '2022-09-30', '2025-12-21'),
    ('MAGS_COURT_FS2025', 'Criminal proceedings - Magistrates court 2025', '2025-12-22', NULL)
ON CONFLICT (scheme_code) DO NOTHING;

-- Fee Scheme for 'Mediation' category
INSERT INTO fee_schemes (scheme_code, scheme_name, valid_from, valid_to)
VALUES
    ('MED_FS2013', 'Mediation Fee Scheme 2013', '2013-04-01', NULL)
ON CONFLICT (scheme_code) DO NOTHING;

-- Fee Scheme for 'Mental Health' category
INSERT INTO fee_schemes (scheme_code, scheme_name, valid_from, valid_to)
VALUES
    ('MHL_DISB_FS2013', 'Mental Health - Disbursement Fee Scheme 2013', '2013-04-01', NULL),
    ('MHL_FS2013', 'Mental Health Fee Scheme 2013', '2013-04-01', NULL)
ON CONFLICT (scheme_code) DO NOTHING;

-- Fee Scheme for 'Miscellaneous' category
INSERT INTO fee_schemes (scheme_code, scheme_name, valid_from, valid_to)
VALUES
    ('MISC_FS2013', 'Miscellaneous Fee Scheme 2013', '2013-04-01', NULL),
    ('MISC_FS2015', 'Miscellaneous Fee Scheme 2015', '2015-03-23', NULL)
ON CONFLICT (scheme_code) DO NOTHING;

-- Fee Scheme for 'Pre Order Cover' category
INSERT INTO fee_schemes (scheme_code, scheme_name, valid_from, valid_to)
VALUES
    ('POC_FS2016', 'Pre Order Cover Fee Scheme 2016', '2016-04-01', '2022-09-29'),
    ('POC_FS2022', 'Pre Order Cover Fee Scheme 2022', '2022-09-30', '2025-12-21'),
    ('POC_FS2025', 'Pre Order Cover Fee Scheme 2025', '2025-12-22', NULL)
    ON CONFLICT (scheme_code) DO NOTHING;

-- Fee Scheme for 'Police Station' category
INSERT INTO fee_schemes (scheme_code, scheme_name, valid_from, valid_to)
VALUES ('POL_FS2016', 'Police Station Work 2016', '2016-04-01', NULL),
       ('POL_FS2021', 'Police Station Work 2021', '2021-06-07', NULL),
       ('POL_FS2022', 'Police Station Work 2022', '2022-09-30', NULL),
       ('POL_FS2024', 'Police Station Work 2024', '2024-12-06', NULL),
       ('POL_FS2025', 'Police Station Work 2025', '2025-12-22', NULL)
ON CONFLICT (scheme_code) DO NOTHING;

-- Fee Scheme for 'Prison Law' category
INSERT INTO fee_schemes (scheme_code, scheme_name, valid_from, valid_to)
VALUES ('PRISON_FS2016', 'Prison Law Fee Scheme 2016', '2016-04-01', '2025-12-21'),
       ('PRISON_FS2025', 'Prison Law Fee Scheme 2025', '2025-12-22', NULL)
ON CONFLICT (scheme_code) DO NOTHING;

-- Fee Scheme for 'Public Law' category
INSERT INTO fee_schemes (scheme_code, scheme_name, valid_from, valid_to)
VALUES
    ('PUB_FS2013', 'Public Law Fee Scheme 2013', '2013-04-01', NULL)
ON CONFLICT (scheme_code) DO NOTHING;

-- Fee Scheme for 'Sending Hearing' category
INSERT INTO fee_schemes (scheme_code, scheme_name, valid_from, valid_to)
VALUES
    ('SEND_HEAR_FS2020', 'Sending Hearing 2020', '2020-10-19', '2022-09-29'),
    ('SEND_HEAR_FS2022', 'Sending Hearing 2022', '2022-09-30', '2025-12-21'),
    ('SEND_HEAR_FS2025', 'Sending Hearing 2025', '2025-12-22', NULL)
ON CONFLICT (scheme_code) DO NOTHING;

-- Fee Scheme for 'Welfare Benefits' category
INSERT INTO fee_schemes (scheme_code, scheme_name, valid_from, valid_to)
VALUES
    ('WB_FS2014', 'Welfare Benefits Fee Scheme 2014', '2014-02-01', '2025-04-30'),
    ('WB_FS2025', 'Welfare Benefits Fee Scheme 2025', '2025-05-01', NULL)
ON CONFLICT (scheme_code) DO NOTHING;

-- Fee Scheme for 'Criminal proceedings - Youth court' category
INSERT INTO fee_schemes (scheme_code, scheme_name, valid_from, valid_to)
VALUES
    ('YOUTH_COURT_FS2024', 'Criminal proceedings - Youth court 2024', '2024-12-06', '2025-12-21'),
    ('YOUTH_COURT_FS2025', 'Criminal proceedings - Youth court 2025', '2025-12-22', NULL)
ON CONFLICT (scheme_code) DO NOTHING;

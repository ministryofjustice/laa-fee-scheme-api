INSERT INTO fee (fee_code, description, fixed_fee, category_type, fee_type, fee_scheme_code, court_designation_type, fee_band_type)
VALUES ('PROE1', 'Representation in the Magistrates Court - category 1A - lower standard fee - undesignated area',
        194.68, 'MAGISTRATES_COURT', 'FIXED', 'MAGS_COURT_FS2016', 'UNDESIGNATED', 'LOWER'),
       ('PROE1', 'Representation in the Magistrates Court - category 1A - lower standard fee - undesignated area',
        223.88, 'MAGISTRATES_COURT', 'FIXED', 'MAGS_COURT_FS2022', 'UNDESIGNATED', 'LOWER'),
       ('PROE2', 'Representation in the Magistrates Court - category 1B - lower standard fee - undesignated area',
        182.01, 'MAGISTRATES_COURT', 'FIXED', 'MAGS_COURT_FS2022', 'UNDESIGNATED', 'LOWER'),
       ('PROE2', 'Representation in the Magistrates Court - category 1B - lower standard fee - undesignated area',
        158.27, 'MAGISTRATES_COURT', 'FIXED', 'MAGS_COURT_FS2016', 'UNDESIGNATED', 'LOWER'),
       ('PROE3', 'Representation in the Magistrates Court - category 2 - lower standard fee - undesignated area',
        321.37, 'MAGISTRATES_COURT', 'FIXED', 'MAGS_COURT_FS2022', 'UNDESIGNATED', 'LOWER'),
       ('PROE3', 'Representation in the Magistrates Court - category 2 - lower standard fee - undesignated area',
        279.45, 'MAGISTRATES_COURT', 'FIXED', 'MAGS_COURT_FS2016', 'UNDESIGNATED', 'LOWER'),
       ('PROF1', 'Representation in the Magistrates Court - category 1A - higher standard fee - undesignated area',
        474.15, 'MAGISTRATES_COURT', 'FIXED', 'MAGS_COURT_FS2022', 'UNDESIGNATED', 'HIGHER'),
       ('PROF1', 'Representation in the Magistrates Court - category 1A - higher standard fee - undesignated area',
        412.30, 'MAGISTRATES_COURT', 'FIXED', 'MAGS_COURT_FS2016', 'UNDESIGNATED', 'HIGHER'),
       ('PROF2', 'Representation in the Magistrates Court - category 1B - higher standard fee - undesignated area',
        380.70, 'MAGISTRATES_COURT', 'FIXED', 'MAGS_COURT_FS2016', 'UNDESIGNATED', 'HIGHER'),
       ('PROF2', 'Representation in the Magistrates Court - category 1B - higher standard fee - undesignated area',
        437.81, 'MAGISTRATES_COURT', 'FIXED', 'MAGS_COURT_FS2022', 'UNDESIGNATED', 'HIGHER')
ON CONFLICT (fee_code, fee_scheme_code) DO NOTHING;

INSERT INTO fee (fee_code, description, fixed_fee, category_type, fee_type, fee_scheme_code, court_designation_type, fee_band_type)
VALUES ('PROF3', 'Representation in the Magistrates Court - category 2 - higher standard fee - undesignated area',
        737.08, 'MAGISTRATES_COURT', 'FIXED', 'MAGS_COURT_FS2022', 'UNDESIGNATED', 'HIGHER'),
       ('PROF3', 'Representation in the Magistrates Court - category 2 - higher standard fee - undesignated area',
        640.94, 'MAGISTRATES_COURT', 'FIXED', 'MAGS_COURT_FS2016', 'UNDESIGNATED', 'HIGHER'),
       ('PROJ1','Representation in the Magistrates Court - second claim for deferred sentence - category 1A - lower standard fee - undesignated area',
        194.68, 'MAGISTRATES_COURT', 'FIXED', 'MAGS_COURT_FS2016', 'UNDESIGNATED', 'LOWER'),
       ('PROJ1','Representation in the Magistrates Court - second claim for deferred sentence - category 1A - lower standard fee - undesignated area',
        223.88, 'MAGISTRATES_COURT', 'FIXED', 'MAGS_COURT_FS2022', 'UNDESIGNATED', 'LOWER'),
       ('PROJ2','Representation in the Magistrates Court - second claim for deferred sentence - category 1B - lower standard fee - undesignated area',
        158.27, 'MAGISTRATES_COURT', 'FIXED', 'MAGS_COURT_FS2016', 'UNDESIGNATED', 'LOWER'),
       ('PROJ2','Representation in the Magistrates Court - second claim for deferred sentence - category 1B - lower standard fee - undesignated area',
        182.01, 'MAGISTRATES_COURT', 'FIXED', 'MAGS_COURT_FS2022', 'UNDESIGNATED', 'LOWER'),
       ('PROJ3','Representation in the Magistrates Court - second claim for deferred sentence - category 1A - higher standard fee - undesignated area',
        474.15, 'MAGISTRATES_COURT', 'FIXED', 'MAGS_COURT_FS2022', 'UNDESIGNATED', 'HIGHER'),
       ('PROJ3','Representation in the Magistrates Court - second claim for deferred sentence - category 1A - higher standard fee - undesignated area',
        412.30, 'MAGISTRATES_COURT', 'FIXED', 'MAGS_COURT_FS2016', 'UNDESIGNATED', 'HIGHER'),
       ('PROJ4','Representation in the Magistrates Court - second claim for deferred sentence - category 1B - higher standard fee - undesignated area',
        437.81, 'MAGISTRATES_COURT', 'FIXED', 'MAGS_COURT_FS2022', 'UNDESIGNATED', 'HIGHER'),
       ('PROJ4','Representation in the Magistrates Court - second claim for deferred sentence - category 1B - higher standard fee - undesignated area',
        380.70, 'MAGISTRATES_COURT', 'FIXED', 'MAGS_COURT_FS2016', 'UNDESIGNATED', 'HIGHER')
ON CONFLICT (fee_code, fee_scheme_code) DO NOTHING;

INSERT INTO fee (fee_code, description, fixed_fee, category_type, fee_type, fee_scheme_code, court_designation_type, fee_band_type)
VALUES ('PROJ5','Representation in the Magistrates Court - second claim for deferred sentence - category 1A - lower standard fee - designated area',
        248.71, 'MAGISTRATES_COURT', 'FIXED', 'MAGS_COURT_FS2016', 'DESIGNATED', 'LOWER'),
       ('PROJ5','Representation in the Magistrates Court - second claim for deferred sentence - category 1A - lower standard fee - designated area',
        286.02, 'MAGISTRATES_COURT', 'FIXED', 'MAGS_COURT_FS2022', 'DESIGNATED', 'LOWER'),
       ('PROJ6','Representation in the Magistrates Court - second claim for deferred sentence - category 1B - lower standard fee - designated area',
        232.53, 'MAGISTRATES_COURT', 'FIXED', 'MAGS_COURT_FS2022', 'DESIGNATED', 'LOWER'),
       ('PROJ6','Representation in the Magistrates Court - second claim for deferred sentence - category 1B - lower standard fee - designated area',
        202.20, 'MAGISTRATES_COURT', 'FIXED', 'MAGS_COURT_FS2016', 'DESIGNATED', 'LOWER'),
       ('PROJ7','Representation in the Magistrates Court - second claim for deferred sentence - category 1A - higher standard fee - designated area',
        542.58, 'MAGISTRATES_COURT', 'FIXED', 'MAGS_COURT_FS2022', 'DESIGNATED', 'HIGHER'),
       ('PROJ7','Representation in the Magistrates Court - second claim for deferred sentence - category 1A - higher standard fee - designated area',
        471.81, 'MAGISTRATES_COURT', 'FIXED', 'MAGS_COURT_FS2016', 'DESIGNATED', 'HIGHER'),
       ('PROJ8','Representation in the Magistrates Court - second claim for deferred sentence - category 1B - higher standard fee - designated area',
        500.99, 'MAGISTRATES_COURT', 'FIXED', 'MAGS_COURT_FS2022', 'DESIGNATED', 'HIGHER'),
       ('PROJ8','Representation in the Magistrates Court - second claim for deferred sentence - category 1B - higher standard fee - designated area',
        435.64, 'MAGISTRATES_COURT', 'FIXED', 'MAGS_COURT_FS2016', 'DESIGNATED', 'HIGHER'),
       ('PROK1', 'Representation in the Magistrates Court - category 1A - lower standard fee - designated area', 248.71,
        'MAGISTRATES_COURT', 'FIXED', 'MAGS_COURT_FS2016', 'DESIGNATED', 'LOWER'),
       ('PROK1', 'Representation in the Magistrates Court - category 1A - lower standard fee - designated area', 286.02,
        'MAGISTRATES_COURT', 'FIXED', 'MAGS_COURT_FS2022', 'DESIGNATED', 'LOWER')
ON CONFLICT (fee_code, fee_scheme_code) DO NOTHING;

INSERT INTO fee (fee_code, description, fixed_fee, category_type, fee_type, fee_scheme_code, court_designation_type, fee_band_type)
VALUES ('PROK2', 'Representation in the Magistrates Court - category 1B - lower standard fee - designated area', 232.53,
        'MAGISTRATES_COURT', 'FIXED', 'MAGS_COURT_FS2022', 'DESIGNATED', 'LOWER'),
       ('PROK2', 'Representation in the Magistrates Court - category 1B - lower standard fee - designated area', 202.20,
        'MAGISTRATES_COURT', 'FIXED', 'MAGS_COURT_FS2016', 'DESIGNATED', 'LOWER'),
       ('PROK3', 'Representation in the Magistrates Court - category 2 - lower standard fee - designated area', 345.34,
        'MAGISTRATES_COURT', 'FIXED', 'MAGS_COURT_FS2016', 'DESIGNATED', 'LOWER'),
       ('PROK3', 'Representation in the Magistrates Court - category 2 - lower standard fee - designated area', 397.14,
        'MAGISTRATES_COURT', 'FIXED', 'MAGS_COURT_FS2022', 'DESIGNATED', 'LOWER'),
       ('PROL1', 'Representation in the Magistrates Court - category 1A - higher standard fee - designated area',
        471.81, 'MAGISTRATES_COURT', 'FIXED', 'MAGS_COURT_FS2016', 'DESIGNATED', 'HIGHER'),
       ('PROL1', 'Representation in the Magistrates Court - category 1A - higher standard fee - designated area',
        542.58, 'MAGISTRATES_COURT', 'FIXED', 'MAGS_COURT_FS2022', 'DESIGNATED', 'HIGHER'),
       ('PROL2', 'Representation in the Magistrates Court - category 1B - higher standard fee - designated area',
        500.99, 'MAGISTRATES_COURT', 'FIXED', 'MAGS_COURT_FS2022', 'DESIGNATED', 'HIGHER'),
       ('PROL2', 'Representation in the Magistrates Court - category 1B - higher standard fee - designated area',
        435.64, 'MAGISTRATES_COURT', 'FIXED', 'MAGS_COURT_FS2016', 'DESIGNATED', 'HIGHER'),
       ('PROL3', 'Representation in the Magistrates Court - category 2 - higher standard fee - designated area', 831.85,
        'MAGISTRATES_COURT', 'FIXED', 'MAGS_COURT_FS2022', 'DESIGNATED', 'HIGHER'),
       ('PROL3', 'Representation in the Magistrates Court - category 2 - higher standard fee - designated area', 723.35,
        'MAGISTRATES_COURT', 'FIXED', 'MAGS_COURT_FS2016', 'DESIGNATED', 'HIGHER')
ON CONFLICT (fee_code, fee_scheme_code) DO NOTHING;

INSERT INTO fee (fee_code, description, fixed_fee, category_type, fee_type, fee_scheme_code, court_designation_type, fee_band_type)
VALUES ('PROV1', 'Breach of part 1 injunctions under ASBCP Act - uncontested - lower standard fee', 158.27,
        'MAGISTRATES_COURT', 'FIXED', 'MAGS_COURT_FS2016', 'UNDESIGNATED', 'LOWER'),
       ('PROV1', 'Breach of part 1 injunctions under ASBCP Act - uncontested - lower standard fee', 182.01,
        'MAGISTRATES_COURT', 'FIXED', 'MAGS_COURT_FS2022', 'UNDESIGNATED', 'LOWER'),
       ('PROV2', 'Breach of part 1 injunctions under ASBCP Act - uncontested - higher standard fee', 437.81,
        'MAGISTRATES_COURT', 'FIXED', 'MAGS_COURT_FS2022', 'UNDESIGNATED', 'HIGHER'),
       ('PROV2', 'Breach of part 1 injunctions under ASBCP Act - uncontested - higher standard fee', 380.70,
        'MAGISTRATES_COURT', 'FIXED', 'MAGS_COURT_FS2016', 'UNDESIGNATED', 'HIGHER'),
       ('PROV3', 'Breach of part 1 injunctions under ASBCP Act - contested - lower standard fee', 279.45,
        'MAGISTRATES_COURT', 'FIXED', 'MAGS_COURT_FS2016', 'UNDESIGNATED', 'LOWER'),
       ('PROV3', 'Breach of part 1 injunctions under ASBCP Act - contested - lower standard fee', 321.37,
        'MAGISTRATES_COURT', 'FIXED', 'MAGS_COURT_FS2022', 'UNDESIGNATED', 'LOWER'),
       ('PROV4', 'Breach of part 1 injunctions under ASBCP Act - contested - higher standard fee', 737.08,
        'MAGISTRATES_COURT', 'FIXED', 'MAGS_COURT_FS2022', 'UNDESIGNATED', 'HIGHER'),
       ('PROV4', 'Breach of part 1 injunctions under ASBCP Act - contested - higher standard fee', 640.94,
        'MAGISTRATES_COURT', 'FIXED', 'MAGS_COURT_FS2016', 'UNDESIGNATED', 'HIGHER'),
       ('YOUE1', 'Youth Representation Order - category 1A - lower standard fee - undesignated area', 822.47,
        'YOUTH_COURT', 'FIXED', 'YOUTH_COURT_FS2024', 'UNDESIGNATED', 'LOWER'),
       ('YOUE2', 'Youth Representation Order - category 1B - lower standard fee - undesignated area', 182.01,
        'YOUTH_COURT', 'FIXED', 'YOUTH_COURT_FS2024', 'UNDESIGNATED', 'LOWER')
ON CONFLICT (fee_code, fee_scheme_code) DO NOTHING;

INSERT INTO fee (fee_code, description, fixed_fee, category_type, fee_type, fee_scheme_code, court_designation_type, fee_band_type)
VALUES ('YOUE3', 'Youth Representation Order - category 2A - lower standard fee - undesignated area', 919.96,
        'YOUTH_COURT', 'FIXED', 'YOUTH_COURT_FS2024', 'UNDESIGNATED', 'LOWER'),
       ('YOUE4', 'Youth Representation Order - category 2B - lower standard fee - undesignated area', 321.37,
        'YOUTH_COURT', 'FIXED', 'YOUTH_COURT_FS2024', 'UNDESIGNATED', 'LOWER'),
       ('YOUF1', 'Youth Representation Order - category 1A - higher standard fee - undesignated area', 1072.74,
        'YOUTH_COURT', 'FIXED', 'YOUTH_COURT_FS2024', 'UNDESIGNATED', 'HIGHER'),
       ('YOUF2', 'Youth Representation Order - category 1B - higher standard fee - undesignated area', 437.81,
        'YOUTH_COURT', 'FIXED', 'YOUTH_COURT_FS2024', 'UNDESIGNATED', 'HIGHER'),
       ('YOUF3', 'Youth Representation Order - category 2A - higher standard fee - undesignated area', 1335.67,
        'YOUTH_COURT', 'FIXED', 'YOUTH_COURT_FS2024', 'UNDESIGNATED', 'HIGHER'),
       ('YOUF4', 'Youth Representation Order - category 2B - higher standard fee - undesignated area', 737.08,
        'YOUTH_COURT', 'FIXED', 'YOUTH_COURT_FS2024', 'UNDESIGNATED', 'HIGHER'),
       ('YOUK1', 'Youth Representation Order - category 1A - lower standard fee - designated area', 884.61,
        'YOUTH_COURT', 'FIXED', 'YOUTH_COURT_FS2024', 'DESIGNATED', 'LOWER'),
       ('YOUK2', 'Youth Representation Order - category 1B - lower standard fee - designated area', 232.53,
        'YOUTH_COURT', 'FIXED', 'YOUTH_COURT_FS2024', 'DESIGNATED', 'LOWER'),
       ('YOUK3', 'Youth Representation Order - category 2A - lower standard fee - designated area', 995.73,
        'YOUTH_COURT', 'FIXED', 'YOUTH_COURT_FS2024', 'DESIGNATED', 'LOWER'),
       ('YOUK4', 'Youth Representation Order - category 2B - lower standard fee - designated area', 397.14,
        'YOUTH_COURT', 'FIXED', 'YOUTH_COURT_FS2024', 'DESIGNATED', 'LOWER')
ON CONFLICT (fee_code, fee_scheme_code) DO NOTHING;

INSERT INTO fee (fee_code, description, fixed_fee, category_type, fee_type, fee_scheme_code, court_designation_type, fee_band_type)
VALUES ('YOUL1', 'Youth Representation Order - category 1A - higher standard fee - designated area', 1141.17,
        'YOUTH_COURT', 'FIXED', 'YOUTH_COURT_FS2024', 'DESIGNATED', 'HIGHER'),
       ('YOUL2','Youth Representation Order - category 1B - higher standard fee - designated area', 500.99,
        'YOUTH_COURT', 'FIXED', 'YOUTH_COURT_FS2024', 'DESIGNATED', 'HIGHER'),
       ('YOUL3','Youth Representation Order - category 2A - higher standard fee - designated area', 1430.44,
        'YOUTH_COURT', 'FIXED', 'YOUTH_COURT_FS2024', 'DESIGNATED', 'HIGHER'),
       ('YOUL4','Youth Representation Order - category 2B - higher standard fee - designated area', 831.85,
        'YOUTH_COURT', 'FIXED', 'YOUTH_COURT_FS2024', 'DESIGNATED', 'HIGHER'),
       ('YOUX1','Youth Representation Order - second claim for deferred sentence - category 1A - lower standard fee - undesignated area',
        822.47, 'YOUTH_COURT', 'FIXED', 'YOUTH_COURT_FS2024', 'UNDESIGNATED', 'LOWER'),
       ('YOUX2','Youth Representation Order - second claim for deferred sentence - category 1B - lower standard fee - undesignated area',
        182.01, 'YOUTH_COURT', 'FIXED', 'YOUTH_COURT_FS2024', 'UNDESIGNATED', 'LOWER'),
       ('YOUX3','Youth Representation Order - second claim for deferred sentence - category 1A - higher standard fee - undesignated area',
        1072.74,'YOUTH_COURT', 'FIXED', 'YOUTH_COURT_FS2024', 'UNDESIGNATED', 'HIGHER'),
       ('YOUX4','Youth Representation Order - second claim for deferred sentence - category 1B - higher standard fee - undesignated area',
        437.81, 'YOUTH_COURT', 'FIXED', 'YOUTH_COURT_FS2024', 'UNDESIGNATED', 'HIGHER'),
       ('YOUY1','Youth Representation Order - second claim for deferred sentence - category 1A - lower standard fee - designated area',
        884.61, 'YOUTH_COURT', 'FIXED', 'YOUTH_COURT_FS2024', 'DESIGNATED', 'LOWER'),
       ('YOUY2','Youth Representation Order - second claim for deferred sentence - category 1B - lower standard fee - designated area',
        232.53, 'YOUTH_COURT', 'FIXED', 'YOUTH_COURT_FS2024', 'DESIGNATED', 'LOWER')
ON CONFLICT (fee_code, fee_scheme_code) DO NOTHING;

INSERT INTO fee (fee_code, description, fixed_fee, category_type, fee_type, fee_scheme_code, court_designation_type, fee_band_type)
VALUES ('YOUY3', 'Youth Representation Order - second claim for deferred sentence - category 1A - higher standard fee - designated area',
        1141.17, 'YOUTH_COURT', 'FIXED', 'YOUTH_COURT_FS2024', 'DESIGNATED', 'HIGHER'),
       ('YOUY4', 'Youth Representation Order - second claim for deferred sentence - category 1B - higher standard fee - designated area',
        500.99, 'YOUTH_COURT', 'FIXED', 'YOUTH_COURT_FS2024', 'DESIGNATED', 'HIGHER')
ON CONFLICT (fee_code, fee_scheme_code) DO NOTHING;

INSERT INTO fee (fee_code, description, fixed_fee, category_type, fee_type, fee_scheme_code)
VALUES ('PROW', 'Sending Hearing Fixed Fee', 181.40, 'SENDING_HEARING', 'FIXED', 'SEND_HEAR_FS2020'),
       ('PROW', 'Sending Hearing Fixed Fee', 208.61, 'SENDING_HEARING', 'FIXED', 'SEND_HEAR_FS2022')
ON CONFLICT (fee_code, fee_scheme_code) DO NOTHING;

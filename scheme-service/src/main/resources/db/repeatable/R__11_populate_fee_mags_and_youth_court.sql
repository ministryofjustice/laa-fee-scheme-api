INSERT INTO fee (fee_code, fixed_fee, fee_scheme_code)
VALUES ('PROE1', 194.68, 'MAGS_COURT_FS2016'),
       ('PROE1', 223.88, 'MAGS_COURT_FS2022'),
       ('PROE2', 182.01, 'MAGS_COURT_FS2022'),
       ('PROE2', 158.27, 'MAGS_COURT_FS2016'),
       ('PROE3', 321.37, 'MAGS_COURT_FS2022'),
       ('PROE3', 279.45, 'MAGS_COURT_FS2016'),
       ('PROF1', 474.15, 'MAGS_COURT_FS2022'),
       ('PROF1', 412.30, 'MAGS_COURT_FS2016'),
       ('PROF2', 380.70, 'MAGS_COURT_FS2016'),
       ('PROF2', 437.81, 'MAGS_COURT_FS2022')
    ON CONFLICT (fee_code, fee_scheme_code) DO NOTHING;

INSERT INTO fee (fee_code, fixed_fee, fee_scheme_code)
VALUES ('PROF3', 737.08, 'MAGS_COURT_FS2022'),
       ('PROF3', 640.94, 'MAGS_COURT_FS2016'),
       ('PROJ1', 194.68, 'MAGS_COURT_FS2016'),
       ('PROJ1', 223.88, 'MAGS_COURT_FS2022'),
       ('PROJ2', 158.27, 'MAGS_COURT_FS2016'),
       ('PROJ2', 182.01, 'MAGS_COURT_FS2022'),
       ('PROJ3', 474.15, 'MAGS_COURT_FS2022'),
       ('PROJ3', 412.30, 'MAGS_COURT_FS2016'),
       ('PROJ4', 437.81, 'MAGS_COURT_FS2022'),
       ('PROJ4', 380.70, 'MAGS_COURT_FS2016')
    ON CONFLICT (fee_code, fee_scheme_code) DO NOTHING;

INSERT INTO fee (fee_code, fixed_fee, fee_scheme_code)
VALUES ('PROJ5', 248.71, 'MAGS_COURT_FS2016'),
       ('PROJ5', 286.02, 'MAGS_COURT_FS2022'),
       ('PROJ6', 232.53, 'MAGS_COURT_FS2022'),
       ('PROJ6', 202.20, 'MAGS_COURT_FS2016'),
       ('PROJ7', 542.58, 'MAGS_COURT_FS2022'),
       ('PROJ7', 471.81, 'MAGS_COURT_FS2016'),
       ('PROJ8', 500.99, 'MAGS_COURT_FS2022'),
       ('PROJ8', 435.64, 'MAGS_COURT_FS2016'),
       ('PROK1', 248.71, 'MAGS_COURT_FS2016'),
       ('PROK1', 286.02, 'MAGS_COURT_FS2022')
    ON CONFLICT (fee_code, fee_scheme_code) DO NOTHING;

INSERT INTO fee (fee_code, fixed_fee, fee_scheme_code)
VALUES ('PROK2', 232.53, 'MAGS_COURT_FS2022'),
       ('PROK2', 202.20, 'MAGS_COURT_FS2016'),
       ('PROK3', 345.34, 'MAGS_COURT_FS2016'),
       ('PROK3', 397.14, 'MAGS_COURT_FS2022'),
       ('PROL1', 471.81, 'MAGS_COURT_FS2016'),
       ('PROL1', 542.58, 'MAGS_COURT_FS2022'),
       ('PROL2', 500.99, 'MAGS_COURT_FS2022'),
       ('PROL2', 435.64, 'MAGS_COURT_FS2016'),
       ('PROL3', 831.85, 'MAGS_COURT_FS2022'),
       ('PROL3', 723.35, 'MAGS_COURT_FS2016')
    ON CONFLICT (fee_code, fee_scheme_code) DO NOTHING;

INSERT INTO fee (fee_code, fixed_fee, fee_scheme_code)
VALUES ('PROV1', 158.27, 'MAGS_COURT_FS2016'),
       ('PROV1', 182.01, 'MAGS_COURT_FS2022'),
       ('PROV2', 437.81, 'MAGS_COURT_FS2022'),
       ('PROV2', 380.70, 'MAGS_COURT_FS2016'),
       ('PROV3', 279.45, 'MAGS_COURT_FS2016'),
       ('PROV3', 321.37, 'MAGS_COURT_FS2022'),
       ('PROV4', 737.08, 'MAGS_COURT_FS2022'),
       ('PROV4', 640.94, 'MAGS_COURT_FS2016'),
       ('YOUE1', 822.47, 'YOUTH_COURT_FS2024'),
       ('YOUE2', 182.01, 'YOUTH_COURT_FS2024')
    ON CONFLICT (fee_code, fee_scheme_code) DO NOTHING;

INSERT INTO fee (fee_code, fixed_fee, fee_scheme_code)
VALUES ('YOUE3', 919.96, 'YOUTH_COURT_FS2024'),
       ('YOUE4', 321.37, 'YOUTH_COURT_FS2024'),
       ('YOUF1', 1072.74, 'YOUTH_COURT_FS2024'),
       ('YOUF2', 437.81, 'YOUTH_COURT_FS2024'),
       ('YOUF3', 1335.67, 'YOUTH_COURT_FS2024'),
       ('YOUF4', 737.08, 'YOUTH_COURT_FS2024'),
       ('YOUK1', 884.61, 'YOUTH_COURT_FS2024'),
       ('YOUK2', 232.53, 'YOUTH_COURT_FS2024'),
       ('YOUK3', 995.73, 'YOUTH_COURT_FS2024'),
       ('YOUK4', 397.14, 'YOUTH_COURT_FS2024')
    ON CONFLICT (fee_code, fee_scheme_code) DO NOTHING;

INSERT INTO fee (fee_code, fixed_fee, fee_scheme_code)
VALUES ('YOUL1', 1141.17, 'YOUTH_COURT_FS2024'),
       ('YOUL2', 500.99, 'YOUTH_COURT_FS2024'),
       ('YOUL3', 1430.44, 'YOUTH_COURT_FS2024'),
       ('YOUL4', 831.85, 'YOUTH_COURT_FS2024'),
       ('YOUX1', 822.47, 'YOUTH_COURT_FS2024'),
       ('YOUX2', 182.01, 'YOUTH_COURT_FS2024'),
       ('YOUX3', 1072.74, 'YOUTH_COURT_FS2024'),
       ('YOUX4', 437.81, 'YOUTH_COURT_FS2024'),
       ('YOUY1', 884.61, 'YOUTH_COURT_FS2024'),
       ('YOUY2', 232.53, 'YOUTH_COURT_FS2024')
    ON CONFLICT (fee_code, fee_scheme_code) DO NOTHING;

INSERT INTO fee (fee_code, fixed_fee, fee_scheme_code)
VALUES ('YOUY3', 1141.17, 'YOUTH_COURT_FS2024'),
       ('YOUY4', 500.99, 'YOUTH_COURT_FS2024')
    ON CONFLICT (fee_code, fee_scheme_code) DO NOTHING;

INSERT INTO fee (fee_code, fixed_fee, fee_scheme_code)
VALUES ('PROW', 181.40, 'SEND_HEAR_FS2020'),
       ('PROW', 208.61, 'SEND_HEAR_FS2022'),
       ('PROW', 229.47, 'SEND_HEAR_FS2025')
    ON CONFLICT (fee_code, fee_scheme_code) DO NOTHING;

-- undesignated magistrates court uplift 2025
INSERT INTO fee (fee_code, fixed_fee, fee_scheme_code)
VALUES ('PROE1', 246.27, 'MAGS_COURT_FS2025'),
       ('PROE2', 200.21, 'MAGS_COURT_FS2025'),
       ('PROE3', 353.51, 'MAGS_COURT_FS2025'),
       ('PROF1', 521.57, 'MAGS_COURT_FS2025'),
       ('PROF2', 481.59, 'MAGS_COURT_FS2025'),
       ('PROF3', 810.79, 'MAGS_COURT_FS2025'),
       ('PROJ1', 246.27, 'MAGS_COURT_FS2025'),
       ('PROJ2', 200.21, 'MAGS_COURT_FS2025'),
       ('PROJ3', 521.57, 'MAGS_COURT_FS2025'),
       ('PROJ4', 481.59, 'MAGS_COURT_FS2025')
ON CONFLICT (fee_code, fee_scheme_code) DO NOTHING;

-- designated magistrates court uplift 2025
INSERT INTO fee (fee_code, fixed_fee, fee_scheme_code)
VALUES ('PROJ5', 314.62, 'MAGS_COURT_FS2025'),
       ('PROJ6', 255.78, 'MAGS_COURT_FS2025'),
       ('PROJ7', 596.84, 'MAGS_COURT_FS2025'),
       ('PROJ8', 551.09, 'MAGS_COURT_FS2025'),
       ('PROK1', 314.62, 'MAGS_COURT_FS2025'),
       ('PROK2', 255.78, 'MAGS_COURT_FS2025'),
       ('PROK3', 436.85, 'MAGS_COURT_FS2025'),
       ('PROL1', 596.84, 'MAGS_COURT_FS2025'),
       ('PROL2', 551.09, 'MAGS_COURT_FS2025'),
       ('PROL3', 915.04, 'MAGS_COURT_FS2025')
ON CONFLICT (fee_code, fee_scheme_code) DO NOTHING;

-- undesignated youth court uplift 2025
INSERT INTO fee (fee_code, fixed_fee, fee_scheme_code)
VALUES ('YOUE1', 904.72, 'YOUTH_COURT_FS2025'),
       ('YOUE2', 200.21, 'YOUTH_COURT_FS2025'),
       ('YOUE3', 1011.96, 'YOUTH_COURT_FS2025'),
       ('YOUE4', 353.51, 'YOUTH_COURT_FS2025'),
       ('YOUF1', 1180.01, 'YOUTH_COURT_FS2025'),
       ('YOUF2', 481.59, 'YOUTH_COURT_FS2025'),
       ('YOUF3', 1469.24, 'YOUTH_COURT_FS2025'),
       ('YOUF4', 810.79, 'YOUTH_COURT_FS2025'),
       ('YOUX1', 904.72, 'YOUTH_COURT_FS2025'),
       ('YOUX2', 200.21, 'YOUTH_COURT_FS2025'),
       ('YOUX3', 1180.01, 'YOUTH_COURT_FS2025'),
       ('YOUX4', 481.59, 'YOUTH_COURT_FS2025')
ON CONFLICT (fee_code, fee_scheme_code) DO NOTHING;

-- designated youth court uplift 2025
INSERT INTO fee (fee_code, fixed_fee, fee_scheme_code)
VALUES ('YOUK1', 973.07, 'YOUTH_COURT_FS2025'),
       ('YOUK2', 255.78, 'YOUTH_COURT_FS2025'),
       ('YOUK3', 1095.30, 'YOUTH_COURT_FS2025'),
       ('YOUK4', 436.85, 'YOUTH_COURT_FS2025'),
       ('YOUL1', 1255.29, 'YOUTH_COURT_FS2025'),
       ('YOUL2', 551.09, 'YOUTH_COURT_FS2025'),
       ('YOUL3', 1573.48, 'YOUTH_COURT_FS2025'),
       ('YOUL4', 915.04, 'YOUTH_COURT_FS2025'),
       ('YOUY1', 973.07, 'YOUTH_COURT_FS2025'),
       ('YOUY2', 255.78, 'YOUTH_COURT_FS2025'),
       ('YOUY3', 1255.29, 'YOUTH_COURT_FS2025'),
       ('YOUY4', 551.09, 'YOUTH_COURT_FS2025')
    ON CONFLICT (fee_code, fee_scheme_code) DO NOTHING;

-- prov1, prov2, prov3, prov4 court uplift 2025, no change in value
INSERT INTO fee (fee_code, fixed_fee, fee_scheme_code)
VALUES ('PROV1', 182.01, 'MAGS_COURT_FS2025'),
       ('PROV2', 437.81, 'MAGS_COURT_FS2025'),
       ('PROV3', 321.37, 'MAGS_COURT_FS2025'),
       ('PROV4', 737.08, 'MAGS_COURT_FS2025')
    ON CONFLICT (fee_code, fee_scheme_code) DO NOTHING;

UPDATE fee
SET lower_standard_fee_limit = CASE fee_code
        WHEN 'PROE1' THEN 272.34
        WHEN 'PROF1' THEN 272.34
        WHEN 'PROE2' THEN 272.34
        WHEN 'PROF2' THEN 272.34
        WHEN 'PROE3' THEN 467.84
        WHEN 'PROF3' THEN 467.84
        WHEN 'PROJ1' THEN 272.34
        WHEN 'PROJ3' THEN 272.34
        WHEN 'PROJ2' THEN 272.34
        WHEN 'PROJ4' THEN 272.34
        WHEN 'PROV1' THEN 272.34
        WHEN 'PROV2' THEN 272.34
        WHEN 'PROV3' THEN 467.84
        WHEN 'PROV4' THEN 467.84
        WHEN 'PROK1' THEN 272.34
        WHEN 'PROL1' THEN 272.34
        WHEN 'PROK2' THEN 272.34
        WHEN 'PROL2' THEN 272.34
        WHEN 'PROK3' THEN 467.84
        WHEN 'PROL3' THEN 467.84
        WHEN 'PROJ5' THEN 272.34
        WHEN 'PROJ7' THEN 272.34
        WHEN 'PROJ6' THEN 272.34
        WHEN 'PROJ8' THEN 272.34
        END,
    higher_standard_fee_limit = CASE fee_code
        WHEN 'PROE1' THEN 471.85
        WHEN 'PROF1' THEN 471.85
        WHEN 'PROE2' THEN 471.85
        WHEN 'PROF2' THEN 471.85
        WHEN 'PROE3' THEN 779.64
        WHEN 'PROF3' THEN 779.64
        WHEN 'PROJ1' THEN 471.85
        WHEN 'PROJ3' THEN 471.85
        WHEN 'PROJ2' THEN 471.85
        WHEN 'PROJ4' THEN 471.85
        WHEN 'PROV1' THEN 471.85
        WHEN 'PROV2' THEN 471.85
        WHEN 'PROV3' THEN 779.64
        WHEN 'PROV4' THEN 779.64
        WHEN 'PROK1' THEN 471.85
        WHEN 'PROL1' THEN 471.85
        WHEN 'PROK2' THEN 471.85
        WHEN 'PROL2' THEN 471.85
        WHEN 'PROK3' THEN 779.64
        WHEN 'PROL3' THEN 779.64
        WHEN 'PROJ5' THEN 471.85
        WHEN 'PROJ7' THEN 471.85
        WHEN 'PROJ6' THEN 471.85
        WHEN 'PROJ8' THEN 471.85
        END
WHERE fee_scheme_code = 'MAGS_COURT_FS2016'
  AND fee_code IN ('PROE1', 'PROF1', 'PROE2', 'PROF2', 'PROE3', 'PROF3',
                   'PROJ1', 'PROJ3', 'PROJ2', 'PROJ4', 'PROV1', 'PROV2',
                   'PROV3', 'PROV4', 'PROK1', 'PROL1', 'PROK2', 'PROL2',
                   'PROK3', 'PROL3', 'PROJ5', 'PROJ7', 'PROJ6', 'PROJ8');

UPDATE fee
SET lower_standard_fee_limit = CASE fee_code
        WHEN 'PROE1' THEN 313.19
        WHEN 'PROF1' THEN 313.19
        WHEN 'PROE2' THEN 313.19
        WHEN 'PROF2' THEN 313.19
        WHEN 'PROE3' THEN 538.02
        WHEN 'PROF3' THEN 538.02
        WHEN 'PROJ1' THEN 313.19
        WHEN 'PROJ3' THEN 313.19
        WHEN 'PROJ2' THEN 313.19
        WHEN 'PROJ4' THEN 313.19
        WHEN 'PROV1' THEN 313.19
        WHEN 'PROV2' THEN 313.19
        WHEN 'PROV3' THEN 538.02
        WHEN 'PROV4' THEN 538.02
        WHEN 'PROK1' THEN 313.19
        WHEN 'PROL1' THEN 313.19
        WHEN 'PROK2' THEN 313.19
        WHEN 'PROL2' THEN 313.19
        WHEN 'PROK3' THEN 538.02
        WHEN 'PROL3' THEN 538.02
        WHEN 'PROJ5' THEN 313.19
        WHEN 'PROJ7' THEN 313.19
        WHEN 'PROJ6' THEN 313.19
        WHEN 'PROJ8' THEN 313.19
        END,
    higher_standard_fee_limit = CASE fee_code
        WHEN 'PROE1' THEN 542.63
        WHEN 'PROF1' THEN 542.63
        WHEN 'PROE2' THEN 542.63
        WHEN 'PROF2' THEN 542.63
        WHEN 'PROE3' THEN 896.59
        WHEN 'PROF3' THEN 896.59
        WHEN 'PROJ1' THEN 542.63
        WHEN 'PROJ3' THEN 542.63
        WHEN 'PROJ2' THEN 542.63
        WHEN 'PROJ4' THEN 542.63
        WHEN 'PROV1' THEN 542.63
        WHEN 'PROV2' THEN 542.63
        WHEN 'PROV3' THEN 896.59
        WHEN 'PROV4' THEN 896.59
        WHEN 'PROK1' THEN 542.63
        WHEN 'PROL1' THEN 542.63
        WHEN 'PROK2' THEN 542.63
        WHEN 'PROL2' THEN 542.63
        WHEN 'PROK3' THEN 896.59
        WHEN 'PROL3' THEN 896.59
        WHEN 'PROJ5' THEN 542.63
        WHEN 'PROJ7' THEN 542.63
        WHEN 'PROJ6' THEN 542.63
        WHEN 'PROJ8' THEN 542.63
        END
WHERE fee_scheme_code = 'MAGS_COURT_FS2022'
  AND fee_code IN ('PROE1', 'PROF1', 'PROE2', 'PROF2', 'PROE3', 'PROF3',
                   'PROJ1', 'PROJ3', 'PROJ2', 'PROJ4', 'PROV1', 'PROV2',
                   'PROV3', 'PROV4', 'PROK1', 'PROL1', 'PROK2', 'PROL2',
                   'PROK3', 'PROL3', 'PROJ5', 'PROJ7', 'PROJ6', 'PROJ8');

UPDATE fee
SET lower_standard_fee_limit = CASE fee_code
        WHEN 'PROE1' THEN 344.51
        WHEN 'PROF1' THEN 344.51
        WHEN 'PROE2' THEN 344.51
        WHEN 'PROF2' THEN 344.51
        WHEN 'PROE3' THEN 591.82
        WHEN 'PROF3' THEN 591.82
        WHEN 'PROJ1' THEN 344.51
        WHEN 'PROJ3' THEN 344.51
        WHEN 'PROJ2' THEN 344.51
        WHEN 'PROJ4' THEN 344.51
        WHEN 'PROV1' THEN 344.51
        WHEN 'PROV2' THEN 344.51
        WHEN 'PROV3' THEN 591.82
        WHEN 'PROV4' THEN 591.82
        WHEN 'PROK1' THEN 344.51
        WHEN 'PROL1' THEN 344.51
        WHEN 'PROK2' THEN 344.51
        WHEN 'PROL2' THEN 344.51
        WHEN 'PROK3' THEN 591.82
        WHEN 'PROL3' THEN 591.82
        WHEN 'PROJ5' THEN 344.51
        WHEN 'PROJ7' THEN 344.51
        WHEN 'PROJ6' THEN 344.51
        WHEN 'PROJ8' THEN 344.51
        END,
    higher_standard_fee_limit = CASE fee_code
        WHEN 'PROE1' THEN 596.89
        WHEN 'PROF1' THEN 596.89
        WHEN 'PROE2' THEN 596.89
        WHEN 'PROF2' THEN 596.89
        WHEN 'PROE3' THEN 986.25
        WHEN 'PROF3' THEN 986.25
        WHEN 'PROJ1' THEN 596.89
        WHEN 'PROJ3' THEN 596.89
        WHEN 'PROJ2' THEN 596.89
        WHEN 'PROJ4' THEN 596.89
        WHEN 'PROV1' THEN 596.89
        WHEN 'PROV2' THEN 596.89
        WHEN 'PROV3' THEN 986.25
        WHEN 'PROV4' THEN 986.25
        WHEN 'PROK1' THEN 596.89
        WHEN 'PROL1' THEN 596.89
        WHEN 'PROK2' THEN 596.89
        WHEN 'PROL2' THEN 596.89
        WHEN 'PROK3' THEN 986.25
        WHEN 'PROL3' THEN 986.25
        WHEN 'PROJ5' THEN 596.89
        WHEN 'PROJ7' THEN 596.89
        WHEN 'PROJ6' THEN 596.89
        WHEN 'PROJ8' THEN 596.89
        END
WHERE fee_scheme_code = 'MAGS_COURT_FS2025'
  AND fee_code IN ('PROE1', 'PROF1', 'PROE2', 'PROF2', 'PROE3', 'PROF3',
                   'PROJ1', 'PROJ3', 'PROJ2', 'PROJ4', 'PROV1', 'PROV2',
                   'PROV3', 'PROV4', 'PROK1', 'PROL1', 'PROK2', 'PROL2',
                   'PROK3', 'PROL3', 'PROJ5', 'PROJ7', 'PROJ6', 'PROJ8');

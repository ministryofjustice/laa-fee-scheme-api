INSERT INTO fee (fee_code, fee_scheme_code, fixed_fee, escape_threshold_limit, total_limit)
VALUES ('PRIA', 'PRISON_FS2016', 200.75, 602.25, NULL),
       ('PRIB1', 'PRISON_FS2016', 203.93, NULL, 357.06),
       ('PRIB2', 'PRISON_FS2016', 564.16, 1691.69, NULL),
       ('PRIC1', 'PRISON_FS2016', 437.21, NULL, 933.93),
       ('PRIC2', 'PRISON_FS2016', 1454.44, 4362.54, NULL),
       ('PRID1', 'PRISON_FS2016', 203.93, NULL, 357.06),
       ('PRID2', 'PRISON_FS2016', 564.16, 1691.69, NULL),
       ('PRIE1', 'PRISON_FS2016', 437.21, NULL, 933.93),
       ('PRIE2', 'PRISON_FS2016', 1454.44, 4362.54, NULL)
ON CONFLICT (fee_code, fee_scheme_code) DO NOTHING;

INSERT INTO fee (fee_code, fee_scheme_code, fixed_fee, escape_threshold_limit, total_limit)
VALUES ('PRIA', 'PRISON_FS2025', 248.93, 746.79, NULL),
       ('PRIB1', 'PRISON_FS2025', 252.87, NULL, 442.75),
       ('PRIB2', 'PRISON_FS2025', 699.56, 2097.70, NULL),
       ('PRIC1', 'PRISON_FS2025', 542.14, NULL, 1158.07),
       ('PRIC2', 'PRISON_FS2025', 1803.51, 5409.55, NULL),
       ('PRID1', 'PRISON_FS2025', 252.87, NULL, 442.75),
       ('PRID2', 'PRISON_FS2025', 699.56, 2097.70, NULL),
       ('PRIE1', 'PRISON_FS2025', 542.14, NULL, 1158.07),
       ('PRIE2', 'PRISON_FS2025', 1803.51, 5409.55, NULL)
ON CONFLICT (fee_code, fee_scheme_code) DO NOTHING;

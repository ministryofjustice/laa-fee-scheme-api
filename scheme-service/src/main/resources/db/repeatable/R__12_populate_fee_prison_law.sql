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

UPDATE fee
SET lower_standard_fee_limit = CASE fee_code
        WHEN 'PRIB1' THEN 357.06
        WHEN 'PRIB2' THEN 357.06
        WHEN 'PRIC1' THEN 933.93
        WHEN 'PRIC2' THEN 933.93
        WHEN 'PRID1' THEN 357.06
        WHEN 'PRID2' THEN 357.06
        WHEN 'PRIE1' THEN 933.93
        WHEN 'PRIE2' THEN 933.93
        END,
    higher_standard_fee_limit = CASE fee_code
        WHEN 'PRIB1' THEN 1691.69
        WHEN 'PRIB2' THEN 1691.69
        WHEN 'PRIC1' THEN 4362.54
        WHEN 'PRIC2' THEN 4362.54
        WHEN 'PRID1' THEN 1691.69
        WHEN 'PRID2' THEN 1691.69
        WHEN 'PRIE1' THEN 4362.54
        WHEN 'PRIE2' THEN 4362.54
        END
WHERE fee_scheme_code = 'PRISON_FS2016'
  AND fee_code IN ('PRIB1', 'PRIB2', 'PRIC1', 'PRIC2', 'PRID1', 'PRID2', 'PRIE1', 'PRIE2');

UPDATE fee
SET lower_standard_fee_limit = CASE fee_code
        WHEN 'PRIB1' THEN 442.75
        WHEN 'PRIB2' THEN 442.75
        WHEN 'PRIC1' THEN 1158.07
        WHEN 'PRIC2' THEN 1158.07
        WHEN 'PRID1' THEN 442.75
        WHEN 'PRID2' THEN 442.75
        WHEN 'PRIE1' THEN 1158.07
        WHEN 'PRIE2' THEN 1158.07
        END,
    higher_standard_fee_limit = CASE fee_code
        WHEN 'PRIB1' THEN 2097.70
        WHEN 'PRIB2' THEN 2097.70
        WHEN 'PRIC1' THEN 5409.55
        WHEN 'PRIC2' THEN 5409.55
        WHEN 'PRID1' THEN 2097.70
        WHEN 'PRID2' THEN 2097.70
        WHEN 'PRIE1' THEN 5409.55
        WHEN 'PRIE2' THEN 5409.55
        END
WHERE fee_scheme_code = 'PRISON_FS2025'
  AND fee_code IN ('PRIB1', 'PRIB2', 'PRIC1', 'PRIC2', 'PRID1', 'PRID2', 'PRIE1', 'PRIE2');

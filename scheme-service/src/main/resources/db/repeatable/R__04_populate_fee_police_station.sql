INSERT INTO fee (fee_code, fee_scheme_code, fixed_fee, upper_cost_limit)
VALUES ('INVA', 'POL_FS2016', NULL, 273.75),
       ('INVE', 'POL_FS2016', NULL, 1368.75),
       ('INVH', 'POL_FS2016', NULL, 1368.75),
       ('INVK', 'POL_FS2016', NULL, 1368.75),
       ('INVL', 'POL_FS2016', NULL, 1368.75),
       ('INVA', 'POL_FS2022', NULL, 314.81),
       ('INVE', 'POL_FS2022', NULL, 1574.06)
ON CONFLICT (fee_code, fee_scheme_code) DO NOTHING;

INSERT INTO fee (fee_code, fee_scheme_code, fixed_fee, upper_cost_limit)
VALUES ('INVH', 'POL_FS2022', NULL, 1574.06),
       ('INVK', 'POL_FS2022', NULL, 1574.06),
       ('INVL', 'POL_FS2022', NULL, 1574.06),
       ('INVM', 'POL_FS2021', NULL, 273.75),
       ('INVB1', 'POL_FS2016', 28.70, NULL),
       ('INVB2', 'POL_FS2016', 27.60, NULL),
       ('INVC', 'POL_FS2016', NULL, NULL)
ON CONFLICT (fee_code, fee_scheme_code) DO NOTHING;

INSERT INTO fee (fee_code, fee_scheme_code, fixed_fee, upper_cost_limit)
VALUES ('INVB1', 'POL_FS2022', 33.00, NULL),
       ('INVB2', 'POL_FS2022', 31.74, NULL),
       ('INVC', 'POL_FS2022', NULL, NULL),
       ('INVM', 'POL_FS2022', NULL, 314.81),
       ('INVB1', 'POL_FS2024', 33.00, NULL),
       ('INVB2', 'POL_FS2024', 31.74, NULL)
ON CONFLICT (fee_code, fee_scheme_code) DO NOTHING;

INSERT INTO fee (fee_code, fee_scheme_code, fixed_fee, upper_cost_limit)
VALUES  ('INVC', 'POL_FS2024', NULL, NULL),
        ('INVC', 'POL_FS2025', NULL, NULL)
ON CONFLICT (fee_code, fee_scheme_code) DO NOTHING;

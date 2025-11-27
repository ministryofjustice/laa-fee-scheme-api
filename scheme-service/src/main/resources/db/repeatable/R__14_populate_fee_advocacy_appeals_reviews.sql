-- Fee Scheme for 2016 'Advocacy Assistance in the Crown Court or Appeals & Reviews' category
INSERT INTO fee
(fee_code, upper_cost_limit, fee_scheme_code)
VALUES
    ('PROH', 1368.75, 'AAR_FS2016'),
    ('APPA', 273.75, 'AAR_FS2016'),
    ('APPB', 456.25, 'AAR_FS2016')
ON CONFLICT (fee_code, fee_scheme_code) DO NOTHING;

-- Fee Scheme for 2022 'Advocacy Assistance in the Crown Court or Appeals & Reviews' category
INSERT INTO fee
(fee_code, upper_cost_limit, fee_scheme_code)
VALUES
    ('PROH', 1574.06, 'AAR_FS2022'),
    ('APPA', 314.81, 'AAR_FS2022'),
    ('APPB', 524.69, 'AAR_FS2022')
ON CONFLICT (fee_code, fee_scheme_code) DO NOTHING;

-- Fee Scheme for 2025 uplift 'Advocacy Assistance in the Crown Court or Appeals & Reviews' category
INSERT INTO fee
(fee_code, upper_cost_limit, fee_scheme_code)
VALUES
    ('PROH1', 1574.06, 'AAR_FS2025'),
    ('PROH2', 1574.06, 'AAR_FS2025'),
    ('APPA', 314.81, 'AAR_FS2025'),
    ('APPB', 524.69, 'AAR_FS2025')
    ON CONFLICT (fee_code, fee_scheme_code) DO NOTHING;
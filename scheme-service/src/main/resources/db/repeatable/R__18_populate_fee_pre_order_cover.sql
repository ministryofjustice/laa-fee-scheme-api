-- Fee Scheme for 2016 'Pre Order Cover' category
INSERT INTO fee
(fee_code, upper_cost_limit, fee_scheme_code)
VALUES
    ('PROP1', 47.95, 'POC_FS2016'),
    ('PROP2', 45.35, 'POC_FS2016')
    ON CONFLICT (fee_code, fee_scheme_code) DO NOTHING;

-- Fee Scheme for 2022 'Pre Order Cover' category
INSERT INTO fee
(fee_code, upper_cost_limit, fee_scheme_code)
VALUES
    ('PROP1', 55.14, 'POC_FS2022'),
    ('PROP2', 52.15, 'POC_FS2022')
ON CONFLICT (fee_code, fee_scheme_code) DO NOTHING;

-- Fee Scheme for 2025 'Pre Order Cover' category
INSERT INTO fee
(fee_code, upper_cost_limit, fee_scheme_code)
VALUES
    ('PROP1', 60.65, 'POC_FS2025'),
    ('PROP2', 57.37, 'POC_FS2025')
    ON CONFLICT (fee_code, fee_scheme_code) DO NOTHING;
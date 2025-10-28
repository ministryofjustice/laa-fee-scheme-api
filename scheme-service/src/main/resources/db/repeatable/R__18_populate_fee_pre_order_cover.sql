
-- Fee Scheme for 2016 'Pre Order Cover' category
INSERT INTO fee
(fee_code, description, total_limit, category_type, fee_type, fee_scheme_code)
VALUES
    ('PROP1', 'Pre Order Cover - London', 47.95, 'PRE_ORDER_COVER', 'HOURLY', 'POC_FS2016'),
    ('PROP2', 'Pre Order Cover - National', 45.35, 'PRE_ORDER_COVER', 'HOURLY', 'POC_FS2016')
    ON CONFLICT (fee_code, fee_scheme_code) DO NOTHING;

-- Fee Scheme for 2022 'Pre Order Cover' category
INSERT INTO fee
(fee_code, description, total_limit, category_type, fee_type, fee_scheme_code)
VALUES
    ('PROP1', 'Pre Order Cover - London', 55.14, 'PRE_ORDER_COVER', 'HOURLY', 'POC_FS2022'),
    ('PROP2', 'Pre Order Cover - National', 52.15, 'PRE_ORDER_COVER', 'HOURLY', 'POC_FS2022')
ON CONFLICT (fee_code, fee_scheme_code) DO NOTHING;
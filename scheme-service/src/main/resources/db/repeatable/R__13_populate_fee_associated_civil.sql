INSERT INTO fee
(fee_code, fixed_fee, escape_threshold_limit, fee_scheme_code)
VALUES
    ('ASMS', 79, 237, 'ASSOC_FS2016'),
    ('ASPL', 259, 777, 'ASSOC_FS2016'),
    ('ASAS', 157, 471, 'ASSOC_FS2016')
ON CONFLICT (fee_code, fee_scheme_code) DO NOTHING;
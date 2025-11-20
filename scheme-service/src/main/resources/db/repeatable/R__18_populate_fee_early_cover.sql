
-- Fee Scheme for 2016 'Early Cover or Refused Means Test' category
INSERT INTO fee
(fee_code, description, fixed_fee, category_type, fee_type, fee_scheme_code)
VALUES
    ('PROT', 'Early Cover', 68.44, 'EARLY_COVER', 'FIXED', 'EC_RMT_FS2016'),
    ('PROU', 'Refused means test – form completion fee', 22.81, 'REFUSED_MEANS_TEST', 'FIXED', 'EC_RMT_FS2016')
    ON CONFLICT (fee_code, fee_scheme_code) DO NOTHING;

-- Fee Scheme for 2022 'Early Cover or Refused Means Test' category
INSERT INTO fee
(fee_code, description, fixed_fee, category_type, fee_type, fee_scheme_code)
VALUES
    ('PROT', 'Early Cover', 78.71, 'EARLY_COVER', 'FIXED', 'EC_RMT_FS2022'),
    ('PROU', 'Refused means test – form completion fee', 26.23, 'REFUSED_MEANS_TEST', 'FIXED', 'EC_RMT_FS2022')
ON CONFLICT (fee_code, fee_scheme_code) DO NOTHING;
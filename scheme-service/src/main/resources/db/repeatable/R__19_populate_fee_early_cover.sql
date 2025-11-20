
-- Fee Scheme for 2016 'Early Cover or Refused Means Test' category
INSERT INTO fee
(fee_code, fixed_fee, fee_scheme_code)
VALUES
    ('PROT', 68.44, 'EC_RMT_FS2016'),
    ('PROU', 22.81, 'EC_RMT_FS2016')
    ON CONFLICT (fee_code, fee_scheme_code) DO NOTHING;

-- Fee Scheme for 2022 'Early Cover or Refused Means Test' category
INSERT INTO fee
(fee_code, fixed_fee, fee_scheme_code)
VALUES
    ('PROT', 78.71, 'EC_RMT_FS2022'),
    ('PROU', 26.23, 'EC_RMT_FS2022')
ON CONFLICT (fee_code, fee_scheme_code) DO NOTHING;
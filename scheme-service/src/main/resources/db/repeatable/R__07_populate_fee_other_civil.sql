INSERT INTO fee (fee_code, fixed_fee, escape_threshold_limit, fee_scheme_code)
VALUES ('CAPA', 239.00, 717.00, 'CAPA_FS2013'),
       ('CLIN', 195.00, 585.00, 'CLIN_FS2013'),
       ('COM', 266.00, 798.00, 'COM_FS2013'),
       ('DEBT', 180.00, 540.00, 'DEBT_FS2013'),
       ('DISC', NULL, 700.00, 'DISC_FS2013'),
       ('ELA', 157.00, 471.00, 'ELA_FS2024'),
       ('HOUS', 157.00, 471.00, 'HOUS_FS2013'),
       ('MISCASBI', 157.00, 471.00, 'MISC_FS2015'),
       ('MISCCON', 159.00, 477.00, 'MISC_FS2013'),
       ('MISCEMP', 207.00, 621.00, 'MISC_FS2013'),
       ('MISCGEN', 79.00, 237.00, 'MISC_FS2013'),
       ('MISCPI', 203.00, 609.00, 'MISC_FS2013'),
       ('PUB', 259.00, 777.00, 'PUB_FS2013'),
       ('WFB1', 208.00, NULL, 'WB_FS2023'),
       ('WFB1', 208.00, 624.00, 'WB_FS2025')
    ON CONFLICT (fee_code, fee_scheme_code) DO NOTHING;

INSERT INTO fee (fee_code, fixed_fee, escape_threshold_limit, fee_scheme_code)
VALUES ('EDUFIN', 272.00, 816.00, 'EDU_FS2013'),
       ('EDUDIS', NULL, NULL, 'EDU_DISB_FS2024')
ON CONFLICT (fee_code, fee_scheme_code) DO NOTHING;

-- Uplifted  Fixed Fees for Debt, Housing ELA and Housing
INSERT INTO fee (fee_code, fixed_fee, escape_threshold_limit, fee_scheme_code)
VALUES ('DEBT', 256.00, 768.00, 'DEBT_FS2025'),
       ('ELA', 223.00, 669.00, 'ELA_FS2025'),
       ('HOUS', 223.00, 669.00, 'HOUS_FS2025')
ON CONFLICT (fee_code, fee_scheme_code) DO NOTHING;
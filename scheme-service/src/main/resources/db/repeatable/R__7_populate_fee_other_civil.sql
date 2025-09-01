INSERT INTO fee (fee_code, description, fixed_fee, escape_threshold_limit, fee_scheme_code, calculation_type, fee_type)
VALUES ('CAPA', 'Claims Against Public Authorities Legal Help Fixed Fee', 239.00, 717.00, 'CAPA_FS2013','CLAIMS_PUBLIC_AUTHORITIES', 'FIXED'),
       ('CLIN', 'Clinical Negligence Legal Help Fixed Fee', 195.00, 585.00, 'CLIN_FS2013', 'CLINICAL_NEGLIGENCE', 'FIXED'),
       ('COM', 'Community Care Legal Help Fixed Fee', 266.00, 798.00, 'COM_FS2013', 'COMMUNITY_CARE', 'FIXED'),
       ('DEBT', 'Debt Legal Help Fixed Fee', 180.00, 540.00, 'DEBT_FS2013', 'DEBT', 'FIXED'),
       ('DISC', 'Discrimination Legal Help Payment', NULL, 700.00, 'DISC_FS2013', 'DISCRIMINATION', 'HOURLY'),
       ('ELA', 'HLPAS Stage One: Early Legal Advice', 157.00, 471.00, 'ELA_FS2024', 'HOUSING_HLPAS', 'FIXED'),
       ('HOUS', 'Housing Fixed Fee', 157.00, 471.00, 'HOUS_FS2013', 'HOUSING', 'FIXED'),
       ('MISCASBI', 'Miscellaneous (ASBI) Legal Help Fixed Fee', 157.00, 471.00, 'MISCASBI_FS2015', 'MISCELLANEOUS', 'FIXED'),
       ('MISCCON', 'Miscellaneous (Consumer) Legal Help Fixed Fee', 159.00, 477.00, 'MISCCON_FS2013', 'MISCELLANEOUS', 'FIXED'),
       ('MISCEMP', 'Miscellaneous (Employment) Legal Help Fixed Fee', 207.00, 621.00, 'MISCEMP_FS2013', 'MISCELLANEOUS', 'FIXED'),
       ('MISCGEN', 'Miscellaneous Legal Help Fixed Fee', 79.00, 237.00, 'MISCGEN_FS2013', 'MISCELLANEOUS', 'FIXED'),
       ('MISCPI', 'Miscellaneous (Personal Injury) Legal Help Fixed Fee', 203.00, 609.00, 'MISCPI_FS2013', 'MISCELLANEOUS', 'FIXED'),
       ('PUB', 'Public Law', 259.00, 777.00, 'PUB_FS2013', 'PUBLIC_LAW', 'FIXED')
ON CONFLICT (fee_code, fee_scheme_code) DO UPDATE
SET
    fee_type = EXCLUDED.fee_type;
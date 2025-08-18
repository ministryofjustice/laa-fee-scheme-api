INSERT INTO fee (fee_code, description, total_fee, escape_threshold_limit, fee_scheme_code, calculation_type)
VALUES ('COM', 'Community Care Legal Help Fixed Fee', 266.00,  798.00, 'COM_FS2013', 'OTHER_CIVIL')
ON CONFLICT (fee_code, fee_scheme_code) DO NOTHING;
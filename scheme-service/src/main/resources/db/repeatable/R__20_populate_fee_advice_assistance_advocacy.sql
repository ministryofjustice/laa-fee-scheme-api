-- Fee Scheme for 2016 'Advice and Assistance and Advocacy Assistance by a court Duty Solicitor' category
INSERT INTO fee
(fee_code, description, category_type, fee_type, fee_scheme_code)
VALUES
('PROD', 'Advice and Assistance and Advocacy Assistance by a court Duty Solicitor', 'ADVICE_ASSISTANCE_ADVOCACY', 'HOURLY', 'AAA_FS2016')
ON CONFLICT (fee_code, fee_scheme_code) DO NOTHING;
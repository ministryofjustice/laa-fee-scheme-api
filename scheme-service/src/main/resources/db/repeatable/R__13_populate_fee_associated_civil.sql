-- Fee Scheme for 2016 'Associated Civil' category
INSERT INTO fee
(fee_code, description, fixed_fee, escape_threshold_limit, category_type, fee_type, fee_scheme_code)
VALUES
    ('ASMS', 'Legal Help and Associated Civil Work – Miscellaneous', 79, 237,'ASSOCIATED_CIVIL', 'FIXED', 'ASSOC_FS2016'),
    ('ASPL', 'Legal Help and Associated Civil Work – Public Law', 259, 777,'ASSOCIATED_CIVIL', 'FIXED', 'ASSOC_FS2016'),
    ('ASAS', 'Part 1 injunction Anti-Social Behaviour Crime and Policing Act 2014', 157, 471,'ASSOCIATED_CIVIL', 'FIXED', 'ASSOC_FS2016')
ON CONFLICT (fee_code, fee_scheme_code) DO NOTHING;
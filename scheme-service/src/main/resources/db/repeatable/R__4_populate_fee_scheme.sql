INSERT INTO fee_schemes (scheme_code, scheme_name, valid_from, valid_to)
VALUES
    ('MED_FS2013', 'mediation fee scheme 2013', '2013-04-01', NULL)
ON CONFLICT (scheme_code) DO NOTHING;
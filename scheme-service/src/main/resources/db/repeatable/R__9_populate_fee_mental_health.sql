INSERT INTO fee (fee_code, description, fixed_fee, escape_threshold_limit, adjorn_hearing_bolt_on, fee_scheme_code, calculation_type)
VALUES ('MH01', 'Non-Mental Health Tribunal Fee', 263.00, 759.00, NULL, 'MHL_FS2013', 'MENTAL_HEALTH'),
       ('MH02', 'Mental Health Tribunal Fee - Level 1 only', 129.00, 387.00, 117.00,'MHL_FS2013', 'MENTAL_HEALTH'),
       ('MH03', 'Mental Health Tribunal Fee - Levels 1 and 2', 450.00, 1350.00, 117.00,'MHL_FS2013', 'MENTAL_HEALTH'),
       ('MH04', 'Mental Health Tribunal Fee - Levels 1, 2 and 3', 744.00, 2232.00, 117.00, 'MHL_FS2013', 'MENTAL_HEALTH'),
       ('MH05', 'Mental Health Tribunal Fee - Level 2 only', 321.00, 963.00, 117.00,'MHL_FS2013', 'MENTAL_HEALTH'),
       ('MH06', 'Mental Health Tribunal Fee - Levels 2 and 3', 615.00, 1845.00, 117.00, 'MHL_FS2013', 'MENTAL_HEALTH'),
       ('MH07', 'Mental Health Tribunal Fee - Level 3 only', 294.00, 882.00, 117.00, 'MHL_FS2013', 'MENTAL_HEALTH'),
       ('MH08', 'Mental Health Tribunal Fee - Levels 1 and 3', 423.00, 1269.00, 117.00, 'MHL_FS2013', 'MENTAL_HEALTH'),
       ('MH10', 'Mental Health Tribunal Fee - Level 1 (Rule 11(7)(a) cases where a patient has not engaged with the provider)', 129.00, NULL,NULL, 'MHL_FS2013', 'MENTAL_HEALTH')
ON CONFLICT (fee_code, fee_scheme_code) DO NOTHING;
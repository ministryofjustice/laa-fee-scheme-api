INSERT INTO fee (fee_code, fee_scheme_code, fixed_fee, upper_cost_limit, description, category_type, fee_type)
VALUES ('INVA', 'POL_FS2016', NULL, 273.75, 'Advice and Assistance (not at the police station)','POLICE_STATION', 'HOURLY'),
       ('INVE', 'POL_FS2016', NULL, 1368.75, 'Warrant of further detention (including armed forces, Terrorism Act 2000, advice & assistance and other police station advice where given)','POLICE_STATION', 'HOURLY'),
       ('INVH', 'POL_FS2016', NULL, 1368.75, 'Police Station: Post-charge attendance','POLICE_STATION', 'HOURLY'),
       ('INVK', 'POL_FS2016', NULL, 1368.75, 'Advocacy Assistance in the magistrates’ court on applications to extend Pre-Charge Bail (Extension to Pre-Charge Bail)','POLICE_STATION', 'HOURLY'),
       ('INVL', 'POL_FS2016', NULL, 1368.75, 'Advocacy Assistance in the magistrates’ court on application to vary Pre-Charge Bail conditions (Varying Pre-Charge Bail)','POLICE_STATION', 'HOURLY'),
       ('INVA', 'POL_FS2022', NULL, 314.81, 'Advice and Assistance (not at the police station)','POLICE_STATION', 'HOURLY'),
       ('INVE', 'POL_FS2022', NULL, 1574.06, 'Warrant of further detention (including armed forces, Terrorism Act 2000, advice & assistance and other police station advice where given)','POLICE_STATION', 'HOURLY')
ON CONFLICT (fee_code, fee_scheme_code) DO UPDATE
SET
   fee_type = EXCLUDED.fee_type;

INSERT INTO fee (fee_code, fee_scheme_code, fixed_fee, upper_cost_limit, description, category_type, fee_type)
VALUES ('INVH', 'POL_FS2022', NULL, 1574.06, 'Police Station: Post-charge attendance','POLICE_STATION', 'HOURLY'),
       ('INVK', 'POL_FS2022', NULL, 1574.06, 'Advocacy Assistance in the magistrates’ court on applications to extend Pre-Charge Bail (Extension to Pre-Charge Bail)','POLICE_STATION', 'HOURLY'),
       ('INVL', 'POL_FS2022', NULL, 1574.06, 'Advocacy Assistance in the magistrates’ court on application to vary Pre-Charge Bail conditions (Varying Pre-Charge Bail)','POLICE_STATION' , 'HOURLY'),
       ('INVM', 'POL_FS2021', NULL, 273.75, 'Pre-Charge Engagement Advice and Assistance UFN Date (for case starting from 07/06/2021)','POLICE_STATION', 'HOURLY'),
       ('INVB1', 'POL_FS2016', 28.70, NULL, 'Police station: telephone advice only (London)','POLICE_STATION', 'FIXED'),
       ('INVB2', 'POL_FS2016', 27.60, NULL, 'Police station: telephone advice only (Outside of London)','POLICE_STATION', 'FIXED'),
       ('INVC', 'POL_FS2016', NULL, NULL, 'Police station: attendance','POLICE_STATION', 'FIXED')
ON CONFLICT (fee_code, fee_scheme_code) DO UPDATE
SET
   fee_type = EXCLUDED.fee_type;

INSERT INTO fee (fee_code, fee_scheme_code, fixed_fee, upper_cost_limit, description, category_type, fee_type)
VALUES ('INVB1', 'POL_FS2022', 33.00, NULL, 'Police station: telephone advice only (London)','POLICE_STATION', 'FIXED'),
       ('INVB2', 'POL_FS2022', 31.74, NULL, 'Police station: telephone advice only (Outside of London)','POLICE_STATION', 'FIXED'),
       ('INVC', 'POL_FS2022', NULL, NULL, 'Police station: attendance','POLICE_STATION', 'FIXED'),
       ('INVM', 'POL_FS2022', NULL, 314.81, 'Pre-Charge Engagement Advice and Assistance UFN Date (for case starting from 07/06/2021)','POLICE_STATION', 'HOURLY'),
       ('INVB1', 'POL_FS2024', 33.00, NULL, 'Police station: telephone advice only (London)','POLICE_STATION', 'FIXED'),
       ('INVB2', 'POL_FS2024', 31.74, NULL, 'Police station: telephone advice only (Outside of London)','POLICE_STATION', 'FIXED')
ON CONFLICT (fee_code, fee_scheme_code) DO UPDATE
SET
   fee_type = EXCLUDED.fee_type;

INSERT INTO fee (fee_code, fee_scheme_code, fixed_fee, upper_cost_limit, description, category_type, fee_type)
VALUES  ('INVC', 'POL_FS2024', NULL, NULL, 'Police station: attendance','POLICE_STATION', 'FIXED'),
        ('INVC', 'TBD', NULL, NULL, 'Police station: attendance','POLICE_STATION', 'FIXED')
ON CONFLICT (fee_code, fee_scheme_code) DO UPDATE
SET
    fee_type = EXCLUDED.fee_type;

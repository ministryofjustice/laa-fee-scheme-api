DELETE FROM fee WHERE fee_code IN ('INVF', 'INVI', 'INVD','INVJ','INVE','INVK','INVB1','INVB2');
-- for fee codes 'INVE','INVK','INVB1','INVB2', To avoid multiple update statements, records will be inserted after removal

INSERT INTO fee (fee_code, fee_scheme_code, fixed_fee, profit_cost_limit, disbursement_limit, escape_threshold_limit,
                 prior_authority_applicable, schedule_reference, ho_interview_bolt_on, oral_cmrh_bolt_on,
                 telephone_cmrh_bolt_on, substantive_hearing_bolt_on, adjorn_hearing_bolt_on, mediation_fee_lower,
                 mediation_fee_higher, region, description, calculation_type, fee_type)
VALUES ('INVA', 'POL_FS2016', NULL, 273.75, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
        'Advice and Assistance (not at the police station)','POLICE_STATION', 'HOURLY'),
       ('INVE', 'POL_FS2016', NULL, 1368.75, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
        'Warrant of further detention (including armed forces, Terrorism Act 2000, advice & assistance and other police station advice where given)','POLICE_STATION', 'HOURLY'),
       ('INVH', 'POL_FS2016', NULL, 1368.75, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
        'Police Station: Post-charge attendance','POLICE_STATION', 'HOURLY'),
       ('INVK', 'POL_FS2016', NULL, 1368.75, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
        'Advocacy Assistance in the magistrates’ court on applications to extend Pre-Charge Bail (Extension to Pre-Charge Bail)','POLICE_STATION', 'HOURLY'),
       ('INVL', 'POL_FS2016', NULL, 1368.75, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
        'Advocacy Assistance in the magistrates’ court on application to vary Pre-Charge Bail conditions (Varying Pre-Charge Bail)','POLICE_STATION', 'HOURLY'),
       ('INVA', 'POL_FS2022', NULL, 314.81, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
        'Advice and Assistance (not at the police station)','POLICE_STATION', 'HOURLY'),
       ('INVE', 'POL_FS2022', NULL, 1574.06, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
        'Warrant of further detention (including armed forces, Terrorism Act 2000, advice & assistance and other police station advice where given)','POLICE_STATION', 'HOURLY')
ON CONFLICT (fee_code, fee_scheme_code) DO UPDATE
SET
   fee_type = EXCLUDED.fee_type;

INSERT INTO fee (fee_code, fee_scheme_code, fixed_fee, profit_cost_limit, disbursement_limit, escape_threshold_limit,
                 prior_authority_applicable, schedule_reference, ho_interview_bolt_on, oral_cmrh_bolt_on,
                 telephone_cmrh_bolt_on, substantive_hearing_bolt_on, adjorn_hearing_bolt_on, mediation_fee_lower,
                 mediation_fee_higher, region, description, calculation_type, fee_type)
VALUES ('INVH', 'POL_FS2022', NULL, 1574.06, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
        'Police Station: Post-charge attendance','POLICE_STATION', 'HOURLY'),
       ('INVK', 'POL_FS2022', NULL, 1574.06, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
        'Advocacy Assistance in the magistrates’ court on applications to extend Pre-Charge Bail (Extension to Pre-Charge Bail)','POLICE_STATION', 'HOURLY'),
       ('INVL', 'POL_FS2022', NULL, 1574.06, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
        'Advocacy Assistance in the magistrates’ court on application to vary Pre-Charge Bail conditions (Varying Pre-Charge Bail)','POLICE_STATION' , 'HOURLY'),
       ('INVM', 'POL_FS2021', NULL, 273.75, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
        'Pre-Charge Engagement Advice and Assistance UFN Date (for case starting from 07/06/2021)','POLICE_STATION', 'HOURLY'),
       ('INVB1', 'POL_FS2016', 28.70, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
        'Police station: telephone advice only (London)','POLICE_STATION', 'FIXED'),
       ('INVB2', 'POL_FS2016', 27.60, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
        'Police station: telephone advice only (Outside of London)','POLICE_STATION', 'FIXED'),
       ('INVC', 'POL_FS2016', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
        'Police station: attendance','POLICE_STATION', 'FIXED')
ON CONFLICT (fee_code, fee_scheme_code) DO UPDATE
SET
   fee_type = EXCLUDED.fee_type;

INSERT INTO fee (fee_code, fee_scheme_code, fixed_fee, profit_cost_limit, disbursement_limit, escape_threshold_limit,
                 prior_authority_applicable, schedule_reference, ho_interview_bolt_on, oral_cmrh_bolt_on,
                 telephone_cmrh_bolt_on, substantive_hearing_bolt_on, adjorn_hearing_bolt_on, mediation_fee_lower,
                 mediation_fee_higher, region, description, calculation_type, fee_type)
VALUES ('INVB1', 'POL_FS2022', 33.00, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
        'Police station: telephone advice only (London)','POLICE_STATION', 'FIXED'),
       ('INVB2', 'POL_FS2022', 31.74, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
        'Police station: telephone advice only (Outside of London)','POLICE_STATION', 'FIXED'),
       ('INVC', 'POL_FS2022', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
        'Police station: attendance','POLICE_STATION', 'FIXED'),
       ('INVM', 'POL_FS2022', NULL, 314.81, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
        'Pre-Charge Engagement Advice and Assistance UFN Date (for case starting from 07/06/2021)','POLICE_STATION', 'HOURLY'),
       ('INVB1', 'POL_FS2024', 33.00, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
        'Police station: telephone advice only (London)','POLICE_STATION', 'FIXED'),
       ('INVB2', 'POL_FS2024', 31.74, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
        'Police station: telephone advice only (Outside of London)','POLICE_STATION', 'FIXED')
ON CONFLICT (fee_code, fee_scheme_code) DO UPDATE
SET
   fee_type = EXCLUDED.fee_type;

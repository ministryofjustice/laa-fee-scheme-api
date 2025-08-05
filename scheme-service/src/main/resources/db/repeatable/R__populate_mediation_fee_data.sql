INSERT INTO "fee-scheme".fee (fee_code, total_fee, mediation_session_one, mediation_session_two, fee_scheme_code)
VALUES
    ('MAM1', 87.00, NULL, NULL, 'MED_FS2013'),
    ('MAM2', 87.00, NULL, NULL, 'MED_FS2013'),
    ('MAM3', 130.00, NULL, NULL, 'MED_FS2013'),
    ('MED1', NULL, 168.00, 756.00, 'MED_FS2013'),
    ('MED2', NULL, 168.00, 462.00, 'MED_FS2013'),
    ('MED3', NULL, 230.00, 1064.00, 'MED_FS2013'),
    ('MED4', NULL, 230.00, 647.00, 'MED_FS2013'),
    ('MED5', NULL, 420.00, 1008.00, 'MED_FS2013'),
    ('MED6', NULL, 294.00, 588.00,  'MED_FS2013'),
    ('MED7', NULL, 357.00, 945.00,  'MED_FS2013'),
    ('MED8', NULL, 262.50, 556.50,  'MED_FS2013'),
    ('MED9', NULL, 294.00, 588.00,  'MED_FS2013'),
    ('MED10', NULL, 231.00, 525.00,  'MED_FS2013'),
    ('MED11', NULL, 482.00, 1316.00,  'MED_FS2013'),
    ('MED12', NULL, 356.00, 773.00,  'MED_FS2013'),
    ('MED13', NULL, 419.00, 1253.00,  'MED_FS2013'),
    ('MED14', NULL, 324.50, 741.50,  'MED_FS2013');

INSERT INTO "fee-scheme".fee (fee_code, total_fee, mediation_session_one, mediation_session_two, fee_scheme_code)
VALUES
    ('MED15', NULL, 356.00, 1190.00,  'MED_FS2013'),
    ('MED16', NULL, 293.00, 710.00,  'MED_FS2013'),
    ('MED17', NULL, 168.00, 588.00,  'MED_FS2013'),
    ('MED18', NULL, 168.00, 378.00,  'MED_FS2013'),
    ('MED19', NULL, 230.00, 834.00,  'MED_FS2013'),
    ('MED20', NULL, 230.00, 532.00,  'MED_FS2013'),
    ('MED21', NULL, 168.00, 651.00,  'MED_FS2013'),
    ('MED22', NULL, 168.00, 378.00,  'MED_FS2013'),
    ('MED23', NULL, 230.00, 834.00,  'MED_FS2013'),
    ('MED24', NULL, 230.00, 721.00,  'MED_FS2013'),
    ('MED25', NULL, 168.00, 462.00,  'MED_FS2013'),
    ('MED26', NULL, 168.00, 315.00,  'MED_FS2013'),
    ('MED27', NULL, 230.00, 647.00,  'MED_FS2013'),
    ('MED28', NULL, 230.00, 438.50,  'MED_FS2013'),
    ('MED29', NULL, 294.00, 588.00,  'MED_FS2013'),
    ('MED30', NULL, 231.00, 378.00,  'MED_FS2013'),
    ('MED31', NULL, 356.00, 773.00,  'MED_FS2013'),
    ('MED32', NULL, 293.00, 501.50,  'MED_FS2013');

--
-- INSERT INTO "fee-scheme".fee (fee_code, total_fee, mediation_session_one, mediation_session_two, fee_scheme_code)
-- VALUES
--
--     \\ Use If existing data needs modified \\
--
--     ON CONFLICT (fee_code) DO UPDATE SET
--     total_fee = EXCLUDED.total_fee,
--                                   mediation_session_one = EXCLUDED.mediation_session_one,
--                                   mediation_session_two = EXCLUDED.mediation_session_two,
--                                   fee_scheme_code = EXCLUDED.fee_scheme_code;
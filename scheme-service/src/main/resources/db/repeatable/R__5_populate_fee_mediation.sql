DELETE FROM fee
WHERE fee_scheme_code = 'MED_FS2013'
  AND fee_code IN ('MAM1','MAM2','MAM3','MED1','MED2','MED3','MED4','MED5','MED6','MED7',
                   'MED8','MED9','MED10','MED11','MED12','MED13','MED14','MED15','MED16',
                   'MED17','MED18','MED19','MED20','MED21','MED22','MED23','MED24','MED25',
                   'MED26','MED27','MED28','MED29','MED30','MED31','MED32');

INSERT INTO fee (fee_code, description, fixed_fee, mediation_fee_lower, mediation_fee_higher, fee_scheme_code, calculation_type, fee_type)
VALUES ('ASSA', 'Mediation Assesment (alone)', 87.00, NULL, NULL, 'MED_FS2013', 'MEDIATION', 'FIXED'),
       ('ASSS', 'Mediation Assesment (separate)', 87.00, NULL, NULL, 'MED_FS2013', 'MEDIATION', 'FIXED'),
       ('ASST', 'Mediation Assesment (together)', 130.00, NULL, NULL, 'MED_FS2013', 'MEDIATION', 'FIXED'),
       ('MDAS2B', 'All Issues Sole -  2 parties eligible, no agreement', NULL, 168.00, 756.00, 'MED_FS2013', 'MEDIATION', 'FIXED'),
       ('MDAS1B', 'All Issues sole - 1 party eligible, no agreement', NULL, 168.00, 462.00, 'MED_FS2013', 'MEDIATION', 'FIXED'),
       ('MDAC2B', 'All Issues Co - 2 parties eligible, no agreement', NULL, 230.00, 1064.00, 'MED_FS2013', 'MEDIATION', 'FIXED'),
       ('MDAC1B', 'All Issues Co -1 party eligible, no agreement', NULL, 230.00, 647.00, 'MED_FS2013', 'MEDIATION', 'FIXED'),
       ('MDAS2S', 'All issues sole -  2 parties eligible, agreement on all issues', NULL, 420.00, 1008.00, 'MED_FS2013', 'MEDIATION', 'FIXED'),
       ('MDAS1S', 'All issues sole -  1 party eligible, agreement on all issues', NULL, 294.00, 588.00, 'MED_FS2013', 'MEDIATION', 'FIXED'),
       ('MDAS2P', 'All issues sole - 2 parties eligible, agreement on P&F only', NULL, 357.00, 945.00, 'MED_FS2013', 'MEDIATION', 'FIXED'),
       ('MDAS1P', 'All issues sole -  1 party eligible, agreement on P&F only', NULL, 262.50, 556.50, 'MED_FS2013', 'MEDIATION', 'FIXED'),
       ('MDAS2C', 'All issues sole - 2 parties eligible, agreement on Child only ', NULL, 294.00, 588.00, 'MED_FS2013', 'MEDIATION', 'FIXED'),
       ('MDAS1C', 'All issues sole - 1 party eligible, agreement on Child only ', NULL, 231.00, 525.00, 'MED_FS2013', 'MEDIATION', 'FIXED'),
       ('MDAC2S', 'All issues Co-Mediation -  2 parties eligible, agreement on all issues', NULL, 482.00, 1316.00, 'MED_FS2013', 'MEDIATION', 'FIXED'),
       ('MDAC1S', 'All issues co-mediation -  1 party eligible, agreement on all issues', NULL, 356.00, 773.00, 'MED_FS2013', 'MEDIATION', 'FIXED'),
       ('MDAC2P', 'All issues co-mediation - 2 parties eligible, agreement on P&F only', NULL, 419.00, 1253.00, 'MED_FS2013', 'MEDIATION', 'FIXED'),
       ('MDAC1P', 'All issues co-mediation - 1 party eligible, agreement on P&F only', NULL, 324.50, 741.50, 'MED_FS2013', 'MEDIATION', 'FIXED'),
       ('MDAC2C', 'All issues co-mediation - 2 parties eligible, agreement on Child only ', NULL, 356.00, 1190.00, 'MED_FS2013', 'MEDIATION', 'FIXED')
    ON CONFLICT (fee_code, fee_scheme_code) DO UPDATE
   SET
       description = EXCLUDED.description,
       fixed_fee = EXCLUDED.fixed_fee,
       mediation_fee_lower = EXCLUDED.mediation_fee_lower,
       mediation_fee_higher = EXCLUDED.mediation_fee_higher,
       calculation_type = EXCLUDED.calculation_type,
       fee_type = EXCLUDED.fee_type;

INSERT INTO fee (fee_code, description, fixed_fee, mediation_fee_lower, mediation_fee_higher, fee_scheme_code, calculation_type, fee_type)
VALUES ('MDAC1C', 'All issues co-mediation -  1 party eligible, agreement on Child only ', NULL, 293.00, 710.00, 'MED_FS2013', 'MEDIATION', 'FIXED'),
       ('MDPS2B', 'Property & Finance sole -  2 parties eligible, no agreement', NULL, 168.00, 588.00, 'MED_FS2013', 'MEDIATION', 'FIXED'),
       ('MDPS1B', 'Property & Finance sole -  1 party eligible, no agreement', NULL, 168.00, 378.00, 'MED_FS2013', 'MEDIATION', 'FIXED'),
       ('MDPC2B', 'Property & Finance Co - 2 parties eligible, no agreement', NULL, 230.00, 834.00, 'MED_FS2013', 'MEDIATION', 'FIXED'),
       ('MDPC1B', 'Property & Finance Co - 1 party eligible, no agreement', NULL, 230.00, 532.00, 'MED_FS2013', 'MEDIATION', 'FIXED'),
       ('MDPS2S', 'Property & Finance sole -  2 parties eligible, with agreed proposal', NULL, 168.00, 651.00, 'MED_FS2013', 'MEDIATION', 'FIXED'),
       ('MDPS1S', 'Property & Finance sole -1 party eligible, with agreed proposal', NULL, 168.00, 378.00, 'MED_FS2013', 'MEDIATION', 'FIXED'),
       ('MDPC2S', 'Property & Finance Co -  2 parties eligible, with agreed proposal', NULL, 230.00, 834.00, 'MED_FS2013', 'MEDIATION', 'FIXED'),
       ('MDPC1S', 'Property & Finance Co -  1 party eligible, with agreed proposal', NULL, 230.00, 721.00, 'MED_FS2013', 'MEDIATION', 'FIXED'),
       ('MDCS2B', 'Child only sole -2 parties eligible, no agreement', NULL, 168.00, 462.00, 'MED_FS2013', 'MEDIATION', 'FIXED'),
       ('MDCS1B', 'Child only sole - 1 party eligible, no agreement', NULL, 168.00, 315.00, 'MED_FS2013', 'MEDIATION', 'FIXED'),
       ('MDCC2B', 'Child only Co - 2 parties eligible, no agreement', NULL, 230.00, 647.00, 'MED_FS2013', 'MEDIATION', 'FIXED'),
       ('MDCC1B', 'Child only Co -  1 party eligible, no agreement', NULL, 230.00, 438.50, 'MED_FS2013', 'MEDIATION', 'FIXED'),
       ('MDCS2S', 'Child only Sole - 2 parties eligible, with agreed proposal', NULL, 294.00, 588.00, 'MED_FS2013', 'MEDIATION', 'FIXED'),
       ('MDCS1S', 'Child only Sole - 1 party eligible, with agreed proposal', NULL, 231.00, 378.00, 'MED_FS2013', 'MEDIATION', 'FIXED'),
       ('MDCC2S', 'Child only Co - 2 parties eligible, with agreed proposal', NULL, 356.00, 773.00, 'MED_FS2013', 'MEDIATION', 'FIXED'),
       ('MDCC1S', 'Child only Co - 1 party eligible, with agreed proposal', NULL, 293.00, 501.50, 'MED_FS2013', 'MEDIATION', 'FIXED')
    ON CONFLICT (fee_code, fee_scheme_code) DO UPDATE
   SET
       description = EXCLUDED.description,
       fixed_fee = EXCLUDED.fixed_fee,
       mediation_fee_lower = EXCLUDED.mediation_fee_lower,
       mediation_fee_higher = EXCLUDED.mediation_fee_higher,
       calculation_type = EXCLUDED.calculation_type,
       fee_type = EXCLUDED.fee_type;
INSERT INTO fee (fee_code, description, total_fee, mediation_session_one, mediation_session_two, fee_scheme_code)
VALUES
    ('MAM1', 'Mediation Assesment (alone)', 87.00, NULL, NULL, 'MED_FS2013'),
    ('MAM2', 'Mediation Assesment (separate)', 87.00, NULL, NULL, 'MED_FS2013'),
    ('MAM3', 'Mediation Assesment (together)', 130.00, NULL, NULL, 'MED_FS2013'),
    ('MED1', 'All Issues sole -  2 parties eligible, no agreement', NULL, 168.00, 756.00, 'MED_FS2013'),
    ('MED2', 'All Issues sole - 1 party eligible, no agreement', NULL, 168.00, 462.00, 'MED_FS2013'),
    ('MED3', 'All Issues Co - 2 parties eligible, no agreement', NULL, 230.00, 1064.00, 'MED_FS2013'),
    ('MED4', 'All Issues Co -1 party eligible, no agreement', NULL, 230.00, 647.00, 'MED_FS2013'),
    ('MED5', 'All issues sole -  2 parties eligible - Agreement on all issues', NULL, 420.00, 1008.00, 'MED_FS2013'),
    ('MED6', 'All issues sole -  1 party eligible - Agreement on all issues', NULL, 294.00, 588.00, 'MED_FS2013'),
    ('MED7', 'All issues sole - 2 parties eligible, agreement on P&F only', NULL, 357.00, 945.00, 'MED_FS2013'),
    ('MED8', 'All issues sole -  1 party eligible, agreement on P&F only', NULL, 262.50, 556.50, 'MED_FS2013'),
    ('MED9', 'All issues sole - 2 parties eligible, agreement on Children only ', NULL, 294.00, 588.00, 'MED_FS2013'),
    ('MED10', 'All issues sole - 1 party eligible, agreement on Children only ', NULL, 231.00, 525.00, 'MED_FS2013'),
    ('MED11', 'All issues Co-Mediation -  2 parties eligible - Agreement on all issues', NULL, 482.00, 1316.00, 'MED_FS2013'),
    ('MED12', 'All issues co-mediation -  1 party eligible - Agreement on all issues', NULL, 356.00, 773.00, 'MED_FS2013'),
    ('MED13', 'All issues co-mediation - 2 parties eligible, agreement on P&F only', NULL, 419.00, 1253.00, 'MED_FS2013'),
    ('MED14', 'All issues co-mediation - 1 party eligible, agreement on P&F only', NULL, 324.50, 741.50, 'MED_FS2013'),
    ('MED15', 'All issues co-mediation - 2 parties eligible, agreement on Children only ', NULL, 356.00, 1190.00, 'MED_FS2013')
ON CONFLICT (fee_code) DO NOTHING;

INSERT INTO fee (fee_code, description, total_fee, mediation_session_one, mediation_session_two, fee_scheme_code)
VALUES
    ('MED16', 'All issues co-mediation -  1 party eligible, agreement on Children only ', NULL, 293.00, 710.00, 'MED_FS2013'),
    ('MED17', 'Property & Finance sole -  2 parties eligible, no agreement', NULL, 168.00, 588.00, 'MED_FS2013'),
    ('MED18', 'Property & Finance sole -  1 party eligible, no agreement', NULL, 168.00, 378.00, 'MED_FS2013'),
    ('MED19', 'Property & Finance Co - 2 parties eligible, no agreement', NULL, 230.00, 834.00, 'MED_FS2013'),
    ('MED20', 'Property & Finance Co - 1 party eligible, no agreement', NULL, 230.00, 532.00, 'MED_FS2013'),
    ('MED21', 'Property & Finance sole -  2 parties eligible, with agreed proposal', NULL, 168.00, 651.00, 'MED_FS2013'),
    ('MED22', 'Property & Finance sole -1 party eligible, with agreed proposal', NULL, 168.00, 378.00, 'MED_FS2013'),
    ('MED23', 'Property & Finance Co -  2 parties eligible, with agreed proposal', NULL, 230.00, 834.00, 'MED_FS2013'),
    ('MED24', 'Property & Finance Co -  1 party eligible, with agreed proposal', NULL, 230.00, 721.00, 'MED_FS2013'),
    ('MED25', 'Child only sole -2 parties eligible, no agreement', NULL, 168.00, 462.00, 'MED_FS2013'),
    ('MED26', 'Child only sole - 1 party eligible, no agreement', NULL, 168.00, 315.00, 'MED_FS2013'),
    ('MED27', 'Child only Co - 2 parties eligible, no agreement', NULL, 230.00, 647.00, 'MED_FS2013'),
    ('MED28', 'Child only Co -  1 party eligible, no agreement', NULL, 230.00, 438.50, 'MED_FS2013'),
    ('MED29', 'Child only sole - single session, 2 parties eligible, with agreed proposal', NULL, 294.00, 588.00, 'MED_FS2013'),
    ('MED30', 'Child only sole - single session 1 party eligible, with agreed proposal', NULL, 231.00, 378.00, 'MED_FS2013'),
    ('MED31', 'Child only Co - single session 2 parties eligible, with agreed proposal', NULL, 356.00, 773.00, 'MED_FS2013'),
    ('MED32', 'Child only Co - single session 1 party eligible, with agreed proposal', NULL, 293.00, 501.50, 'MED_FS2013')
ON CONFLICT (fee_code) DO NOTHING;
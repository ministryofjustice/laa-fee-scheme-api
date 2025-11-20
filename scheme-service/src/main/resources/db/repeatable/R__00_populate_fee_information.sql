INSERT INTO fee_code_information (fee_code, fee_description, fee_type, category_type)
VALUES ('IACA', 'Standard Fee - Asylum CLR  (2a)', 'FIXED', 'IMMIGRATION_ASYLUM'),
       ('IACB', 'Standard Fee - Asylum CLR (2b + advocacy substantive hearing fee) ', 'FIXED', 'IMMIGRATION_ASYLUM'),
       ('IACC', 'Standard Fee - Asylum CLR (2c + advocacy substantive hearing fee) ', 'FIXED', 'IMMIGRATION_ASYLUM'),
       ('IACE', 'Standard Fee - Asylum CLR 2d ', 'FIXED', 'IMMIGRATION_ASYLUM'),
       ('IACF', 'Standard Fee - Asylum CLR (2e + advocacy substantive hearing fee)  ', 'FIXED', 'IMMIGRATION_ASYLUM'),
       ('IALB', 'Standard Fee - Asylum LH ', 'FIXED', 'IMMIGRATION_ASYLUM'),
       ('IMCA', 'Standard Fee - Immigration CLR (2a)', 'FIXED', 'IMMIGRATION_ASYLUM'),
       ('IMCB', 'Standard Fee - Immigration CLR (2b + advocacy substantive hearing fee) ', 'FIXED', 'IMMIGRATION_ASYLUM'),
       ('IMCC', 'Standard Fee - Immigration CLR (2c + advocacy substantive hearing fee)  ', 'FIXED', 'IMMIGRATION_ASYLUM'),
       ('IMCE', 'Standard Fee - Immigration CLR 2d ', 'FIXED', 'IMMIGRATION_ASYLUM'),
       ('IMCF', 'Standard Fee - Immigration CLR (2e + advocacy substantive hearing fee) ', 'FIXED', 'IMMIGRATION_ASYLUM'),
       ('IMLB', 'Standard Fee - Immigration LH ', 'FIXED', 'IMMIGRATION_ASYLUM'),
       ('IDAS1', 'Detained Duty Advice Scheme (1-4 clients seen)', 'FIXED', 'IMMIGRATION_ASYLUM'),
       ('IDAS2', 'Detained Duty Advice Scheme (5+ clients seen)', 'FIXED', 'IMMIGRATION_ASYLUM'),
       ('IAXL', 'LH Hourly Rates - Asylum - £800 PC and £400 disb', 'HOURLY', 'IMMIGRATION_ASYLUM'),
       ('IMXL', 'LH Hourly Rates - Imm - £500 PC and £400 disb', 'HOURLY', 'IMMIGRATION_ASYLUM'),
       ('IA100', 'LH Hourly Rates  - £100 total limit', 'HOURLY', 'IMMIGRATION_ASYLUM'),
       ('IAXC', 'CLR Hourly Rates Asylum - £1600 total limit', 'HOURLY', 'IMMIGRATION_ASYLUM'),
       ('IMXC', 'CLR Hourly Rates Imm - £1200 total limit', 'HOURLY', 'IMMIGRATION_ASYLUM'),
       ('IRAR', 'CLR Upper Tribunal Transitional cases', 'HOURLY', 'IMMIGRATION_ASYLUM'),
       ('IACD', 'Interim hourly rates - Asylum CLR', 'HOURLY', 'IMMIGRATION_ASYLUM'),
       ('IMCD', 'Interim hourly rates - Immigration Interim CLR', 'HOURLY', 'IMMIGRATION_ASYLUM'),
       ('ICASD', 'Asylum CLR Hourly Rates Stage Disbursement', 'DISB_ONLY', 'IMMIGRATION_ASYLUM'),
       ('ICISD', 'Immigration CLR Hourly Rates Stage Disbursement', 'DISB_ONLY', 'IMMIGRATION_ASYLUM'),
       ('ICSSD', 'CLR SFS Stage Disbursement', 'DISB_ONLY', 'IMMIGRATION_ASYLUM'),
       ('ILHSD', 'LH Stage Disbursement', 'DISB_ONLY', 'IMMIGRATION_ASYLUM')
ON CONFLICT (fee_code) DO NOTHING;

INSERT INTO fee_code_information (fee_code, fee_description, fee_type, category_type)
VALUES ('MHL01', 'Non-Mental Health Tribunal Fee', 'FIXED', 'MENTAL_HEALTH'),
       ('MHL02', 'Mental Health Tribunal Fee - Level 1 only', 'FIXED', 'MENTAL_HEALTH'),
       ('MHL03', 'Mental Health Tribunal Fee - Levels 1 and 2', 'FIXED', 'MENTAL_HEALTH'),
       ('MHL04', 'Mental Health Tribunal Fee - Levels 1, 2 and 3', 'FIXED', 'MENTAL_HEALTH'),
       ('MHL05', 'Mental Health Tribunal Fee - Level 2 only', 'FIXED', 'MENTAL_HEALTH'),
       ('MHL06', 'Mental Health Tribunal Fee - Levels 2 and 3', 'FIXED', 'MENTAL_HEALTH'),
       ('MHL07', 'Mental Health Tribunal Fee - Level 3 only', 'FIXED', 'MENTAL_HEALTH'),
       ('MHL08', 'Mental Health Tribunal Fee - Levels 1 and 3', 'FIXED', 'MENTAL_HEALTH'),
       ('MHL10', 'Mental Health Tribunal Fee - Level 1 (Rule 11(7)(a) cases where a patient has not engaged with the provider)','FIXED', 'MENTAL_HEALTH'),
       ('MHLDIS', 'Mental Health - Interim Claim for Disbursements', 'DISB_ONLY', 'MENTAL_HEALTH')
    ON CONFLICT (fee_code) DO NOTHING;

INSERT INTO fee_code_information (fee_code, fee_description, fee_type, category_type)
VALUES ('FPB010', 'Public Family LH Fixed Fee', 'FIXED', 'FAMILY'),
       ('FPB020', 'Public Family FH Fixed Fee (Section 31 Pre-proceedings Only)', 'FIXED', 'FAMILY'),
       ('FPB030', 'Public Family LH+FH (Public Family Help Lower can be claimed for Section 31 Pre-proceedings Only)', 'FIXED', 'FAMILY'),
       ('FVP100', 'Private Family LH Fixed Fee - Divorce Petitioner Only', 'FIXED', 'FAMILY'),
       ('FVP012', 'Private Family LH Fixed Fee - Divorce Respondent Only', 'FIXED', 'FAMILY'),
       ('FVP011', 'Private Family LH Fixed Fee - Domestic Abuse Proceedings', 'FIXED', 'FAMILY'),
       ('FVP013', 'Private Family LH Fixed Fee - Child Abduction (International)', 'FIXED', 'FAMILY'),
       ('FVP010', 'Private Family LH Fixed Fee - Children or Finance', 'FIXED', 'FAMILY'),
       ('FVP110', 'Private Family FH Fixed Fee - Children (settled)', 'FIXED', 'FAMILY'),
       ('FVP130', 'Private Family FH Fixed Fee - Children (not settled)', 'FIXED', 'FAMILY'),
       ('FVP120', 'Private Family FH Fixed Fee - Finance  (settled)', 'FIXED', 'FAMILY'),
       ('FVP140', 'Private Family FH Fixed Fee - Finance  (not settled)', 'FIXED', 'FAMILY'),
       ('FVP150', 'Private Family FH Fixed Fee - Children & Finance (both settled)', 'FIXED', 'FAMILY'),
       ('FVP180', 'Private Family FH Fixed Fee - Children & Finance  (neither settled)', 'FIXED', 'FAMILY'),
       ('FVP160', 'Private Family FH Fixed Fee - Children & Finance (children settled)', 'FIXED', 'FAMILY'),
       ('FVP170', 'Private Family FH Fixed Fee - Children & Finance (finance settled)', 'FIXED', 'FAMILY'),
       ('FVP190', 'Help with Mediation - Advice Only', 'FIXED', 'FAMILY'),
       ('FVP200', 'Help with Mediation - Finance Consent Order Only', 'FIXED', 'FAMILY'),
       ('FVP210', 'Help with Mediation - Advice & Finance Consent Order', 'FIXED', 'FAMILY')
    ON CONFLICT (fee_code) DO NOTHING;

INSERT INTO fee_code_information (fee_code, fee_description, fee_type, category_type)
VALUES ('FVP020', 'Private Family LH Fixed Fee (Children or Finance) + FH Fixed Fee - Children (settled)', 'FIXED', 'FAMILY'),
       ('FVP040', 'Private Family LH Fixed Fee (Children or Finance) + FH Fixed Fee - Children (not settled)', 'FIXED', 'FAMILY'),
       ('FVP030', 'Private Family LH Fixed Fee (Children or Finance) + FH Fixed Fee - Finance (settled)', 'FIXED', 'FAMILY'),
       ('FVP050', 'Private Family LH Fixed Fee (Children or Finance) + FH Fixed Fee - Finance (not settled)', 'FIXED', 'FAMILY'),
       ('FVP060', 'Private Family LH Fixed Fee (Children or Finance) + FH Fixed Fee - Children & Finance (both settled)', 'FIXED', 'FAMILY'),
       ('FVP090', 'Private Family LH Fixed Fee (Children or Finance) + FH Fixed Fee - Children & Finance (neither settled)','FIXED', 'FAMILY'),
       ('FVP070', 'Private Family LH Fixed Fee (Children or Finance) + FH Fixed Fee - Children & Finance (children settled)','FIXED', 'FAMILY'),
       ('FVP080', 'Private Family LH Fixed Fee (Children or Finance) + FH Fixed Fee - Children & Finance (finance settled)','FIXED', 'FAMILY'),
       ('FVP021', 'Private Family LH Fixed Fee (DA Proceedings) + FH Fixed Fee - Children (settled)', 'FIXED', 'FAMILY'),
       ('FVP041', 'Private Family LH Fixed Fee (DA Proceedings) + FH Fixed Fee - Children (not settled)', 'FIXED', 'FAMILY'),
       ('FVP031', 'Private Family LH Fixed Fee (DA Proceedings) + FH Fixed Fee - Finance (settled)', 'FIXED', 'FAMILY'),
       ('FVP051', 'Private Family LH Fixed Fee (DA Proceedings) + FH Fixed Fee - Finance (not settled)', 'FIXED', 'FAMILY'),
       ('FVP061', 'Private Family LH Fixed Fee (DA Proceedings) + FH Fixed Fee - Children & Finance (both settled)', 'FIXED', 'FAMILY'),
       ('FVP091', 'Private Family LH Fixed Fee (DA Proceedings) + FH Fixed Fee - Children & Finance (neither settled)', 'FIXED', 'FAMILY'),
       ('FVP071', 'Private Family LH Fixed Fee (DA Proceedings) + FH Fixed Fee - Children & Finance (children settled)', 'FIXED', 'FAMILY'),
       ('FVP081', 'Private Family LH Fixed Fee (DA Proceedings) + FH Fixed Fee - Children & Finance (finance settled)', 'FIXED', 'FAMILY')
    ON CONFLICT (fee_code) DO NOTHING;

INSERT INTO fee_code_information (fee_code, fee_description, fee_type, category_type)
VALUES ('ASSA', 'Mediation Assesment (alone)', 'FIXED', 'MEDIATION'),
       ('ASSS', 'Mediation Assesment (separate)', 'FIXED', 'MEDIATION'),
       ('ASST', 'Mediation Assesment (together)', 'FIXED', 'MEDIATION'),
       ('MDAS2B', 'All Issues Sole -  2 parties eligible, no agreement', 'FIXED', 'MEDIATION'),
       ('MDAS1B', 'All Issues Sole - 1 party eligible, no agreement', 'FIXED', 'MEDIATION'),
       ('MDAC2B', 'All Issues Co - 2 parties eligible, no agreement', 'FIXED', 'MEDIATION'),
       ('MDAC1B', 'All Issues Co -1 party eligible, no agreement', 'FIXED', 'MEDIATION'),
       ('MDAS2S', 'All Issues Sole -  2 parties eligible, agreement on all Issues', 'FIXED', 'MEDIATION'),
       ('MDAS1S', 'All Issues Sole -  1 party eligible, agreement on all Issues', 'FIXED', 'MEDIATION'),
       ('MDAS2P', 'All Issues Sole - 2 parties eligible, agreement on P&F only', 'FIXED', 'MEDIATION'),
       ('MDAS1P', 'All Issues Sole -  1 party eligible, agreement on P&F only', 'FIXED', 'MEDIATION'),
       ('MDAS2C', 'All Issues Sole - 2 parties eligible, agreement on Child only ', 'FIXED', 'MEDIATION'),
       ('MDAS1C', 'All Issues Sole - 1 party eligible, agreement on Child only ', 'FIXED', 'MEDIATION'),
       ('MDAC2S', 'All Issues Co-Mediation -  2 parties eligible, agreement on all Issues', 'FIXED', 'MEDIATION'),
       ('MDAC1S', 'All Issues Co-mediation -  1 party eligible, agreement on all Issues', 'FIXED', 'MEDIATION'),
       ('MDAC2P', 'All Issues Co-mediation - 2 parties eligible, agreement on P&F only', 'FIXED', 'MEDIATION'),
       ('MDAC1P', 'All Issues Co-mediation - 1 party eligible, agreement on P&F only', 'FIXED', 'MEDIATION'),
       ('MDAC2C', 'All Issues Co-mediation - 2 parties eligible, agreement on Child only ', 'FIXED', 'MEDIATION'),
       ('MDAC1C', 'All Issues Co-mediation -  1 party eligible, agreement on Child only ', 'FIXED', 'MEDIATION')
    ON CONFLICT (fee_code) DO NOTHING;

INSERT INTO fee_code_information (fee_code, fee_description, fee_type, category_type)
VALUES ('MDPS2B', 'Property & Finance Sole -  2 parties eligible, no agreement', 'FIXED', 'MEDIATION'),
       ('MDPS1B', 'Property & Finance Sole -  1 party eligible, no agreement', 'FIXED', 'MEDIATION'),
       ('MDPC2B', 'Property & Finance Co - 2 parties eligible, no agreement', 'FIXED', 'MEDIATION'),
       ('MDPC1B', 'Property & Finance Co - 1 party eligible, no agreement', 'FIXED', 'MEDIATION'),
       ('MDPS2S', 'Property & Finance Sole -  2 parties eligible, with agreed proposal', 'FIXED', 'MEDIATION'),
       ('MDPS1S', 'Property & Finance Sole -1 party eligible, with agreed proposal', 'FIXED', 'MEDIATION'),
       ('MDPC2S', 'Property & Finance Co -  2 parties eligible, with agreed proposal', 'FIXED', 'MEDIATION'),
       ('MDPC1S', 'Property & Finance Co -  1 party eligible, with agreed proposal', 'FIXED', 'MEDIATION'),
       ('MDCS2B', 'Child only Sole -2 parties eligible, no agreement', 'FIXED', 'MEDIATION'),
       ('MDCS1B', 'Child only Sole - 1 party eligible, no agreement', 'FIXED', 'MEDIATION'),
       ('MDCC2B', 'Child only Co - 2 parties eligible, no agreement', 'FIXED', 'MEDIATION'),
       ('MDCC1B', 'Child only Co -  1 party eligible, no agreement', 'FIXED', 'MEDIATION'),
       ('MDCS2S', 'Child only Sole - 2 parties eligible, with agreed proposal', 'FIXED', 'MEDIATION'),
       ('MDCS1S', 'Child only Sole - 1 party eligible, with agreed proposal', 'FIXED', 'MEDIATION'),
       ('MDCC2S', 'Child only Co - 2 parties eligible, with agreed proposal', 'FIXED', 'MEDIATION'),
       ('MDCC1S', 'Child only Co - 1 party eligible, with agreed proposal', 'FIXED', 'MEDIATION')
    ON CONFLICT (fee_code) DO NOTHING;

INSERT INTO fee_code_information (fee_code, fee_description, fee_type, category_type)
VALUES ('COM', 'Community Care Legal Help Fixed Fee', 'FIXED', 'COMMUNITY_CARE'),
       ('CAPA', 'Claims Against Public Authorities Legal Help Fixed Fee', 'FIXED', 'CLAIMS_PUBLIC_AUTHORITIES'),
       ('CLIN', 'Clinical Negligence Legal Help Fixed Fee', 'FIXED', 'CLINICAL_NEGLIGENCE'),
       ('DEBT', 'Debt Legal Help Fixed Fee', 'FIXED', 'DEBT'),
       ('DISC', 'Discrimination Legal Help Payment', 'HOURLY', 'DISCRIMINATION'),
       ('EDUFIN', 'Education Legal Help Fixed Fee', 'FIXED', 'EDUCATION'),
       ('EDUDIS', 'Education - Interim Claim for Disbursement', 'DISB_ONLY', 'EDUCATION'),
       ('ELA', 'HLPAS Stage One: early legal advice', 'FIXED', 'HOUSING_HLPAS'),
       ('HOUS', 'Housing Fixed Fee', 'FIXED', 'HOUSING'),
       ('MISCGEN', 'Miscellaneous Legal Help Fixed Fee', 'FIXED', 'MISCELLANEOUS'),
       ('MISCCON', 'Miscellaneous (Consumer) Legal Help Fixed Fee', 'FIXED', 'MISCELLANEOUS'),
       ('MISCPI', 'Miscellaneous (Personal Injury) Legal Help Fixed Fee', 'FIXED', 'MISCELLANEOUS'),
       ('MISCASBI', 'Miscellaneous (ASBI) Legal Help Fixed Fee', 'FIXED', 'MISCELLANEOUS'),
       ('MISCEMP', 'Miscellaneous (Employment) Legal Help Fixed Fee', 'FIXED', 'MISCELLANEOUS'),
       ('PUB', 'Public Law Legal Help Fixed Fee', 'FIXED', 'PUBLIC_LAW'),
       ('WFB1', 'Welfare Benefits Controlled Work fee', 'FIXED', 'WELFARE_BENEFITS')
    ON CONFLICT (fee_code) DO NOTHING;

INSERT INTO fee_code_information (fee_code, fee_description, fee_type, category_type)
VALUES ('INVC', 'Police station: attendance', 'FIXED', 'POLICE_STATION'),
       ('INVA', 'Advice and Assistance (not at the police station)', 'HOURLY', 'POLICE_STATION'),
       ('INVE', 'Warrant of further detention (including armed forces, Terrorism Act 2000, advice & assistance and other police station advice where given)','HOURLY', 'POLICE_STATION'),
       ('INVH', 'Police Station: Post-charge attendance', 'HOURLY', 'POLICE_STATION'),
       ('INVK', 'Advocacy Assistance in the magistrates’ court on applications to extend Pre-Charge Bail (Extension to Pre-Charge Bail)','HOURLY', 'POLICE_STATION'),
       ('INVL', 'Advocacy Assistance in the magistrates’ court on application to vary Pre-Charge Bail conditions (Varying Pre-Charge Bail)','HOURLY', 'POLICE_STATION'),
       ('INVM', 'Pre-Charge Engagement Advice and Assistance ', 'HOURLY', 'POLICE_STATION'),
       ('INVB1', 'Police station: telephone advice only (London)', 'FIXED', 'POLICE_STATION'),
       ('INVB2', 'Police station: telephone advice only (Outside of London)', 'FIXED', 'POLICE_STATION')
ON CONFLICT (fee_code) DO NOTHING;

-- name
INSERT INTO fee_code_information (fee_code, fee_description, fee_type, category_type)
VALUES ('PROT', 'Early Cover', 'FIXED', 'EARLY_COVER'),
       ('PROU', 'Refused means test – form completion fee', 'FIXED', 'REFUSED_MEANS_TEST'),
       ('PROW', 'Sending Hearing Fixed Fee ', 'FIXED', 'SENDING_HEARING'),
       ('PROD', 'Advice and Assistance and Advocacy Assistance by a court Duty Solicitor', 'FIXED', 'ADVICE_ASSISTANCE_ADVOCACY'),
       ('PROP1', 'Pre Order Cover - London', 'FIXED', 'PRE_ORDER_COVER'),
       ('PROP2', 'Pre Order Cover - National', 'FIXED', 'PRE_ORDER_COVER'),
       ('PROH', 'Advocacy Assistance in the Crown Court', 'FIXED', 'ADVOCACY_APPEALS_REVIEWS')
ON CONFLICT (fee_code) DO NOTHING;

-- Magistrates court
INSERT INTO fee_code_information (fee_code, fee_description, fee_type, category_type, court_designation_type, fee_band_type)
VALUES ('PROE1', 'Representation in the Magistrates Court - category 1A -  lower standard fee - undesignated area','FIXED', 'MAGISTRATES_COURT', 'UNDESIGNATED', 'LOWER'),
       ('PROE2', 'Representation in the Magistrates Court - category 1B -  lower standard fee  - undesignated area', 'FIXED', 'MAGISTRATES_COURT', 'UNDESIGNATED', 'LOWER'),
       ('PROE3', 'Representation in the Magistrates Court - category 2 -  lower standard fee - undesignated area', 'FIXED', 'MAGISTRATES_COURT', 'UNDESIGNATED', 'LOWER'),
       ('PROF1', 'Representation in the Magistrates Court - category 1A -  higher standard fee - undesignated area', 'FIXED', 'MAGISTRATES_COURT', 'UNDESIGNATED', 'HIGHER'),
       ('PROF2', 'Representation in the Magistrates Court - category 1B -  higher standard fee - undesignated area', 'FIXED', 'MAGISTRATES_COURT', 'UNDESIGNATED', 'HIGHER'),
       ('PROF3', 'Representation in the Magistrates Court - category 2 -  higher standard fee - undesignated area',  'FIXED', 'MAGISTRATES_COURT', 'UNDESIGNATED', 'HIGHER'),
       ('PROJ1', 'Representation in the Magistrates Court - second claim for deferred sentence - category 1A -  lower standard fee - undesignated area',  'FIXED', 'MAGISTRATES_COURT', 'UNDESIGNATED', 'LOWER'),
       ('PROJ2', 'Representation in the Magistrates Court - second claim for deferred sentence - category 1B -  lower standard fee - undesignated area',   'FIXED', 'MAGISTRATES_COURT', 'UNDESIGNATED', 'LOWER'),
       ('PROJ3', 'Representation in the Magistrates Court - second claim for deferred sentence -  category 1A -  higher standard fee - undesignated area',  'FIXED', 'MAGISTRATES_COURT', 'UNDESIGNATED', 'HIGHER'),
       ('PROJ4', 'Representation in the Magistrates Court - second claim for deferred sentence - category 1B -  higher standard fee - undesignated area', 'FIXED', 'MAGISTRATES_COURT', 'UNDESIGNATED', 'HIGHER'),
       ('PROJ5', 'Representation in the Magistrates Court - second claim for deferred sentence - category 1A - lower standard fee - designated area', 'FIXED', 'MAGISTRATES_COURT', 'DESIGNATED', 'LOWER'),
       ('PROJ6', 'Representation in the Magistrates Court - second claim for deferred sentence - category 1B - lower standard fee - designated area',  'FIXED', 'MAGISTRATES_COURT', 'DESIGNATED', 'LOWER'),
       ('PROJ7', 'Representation in the Magistrates Court - second claim for deferred sentence - category 1A -  higher standard fee - designated area',  'FIXED', 'MAGISTRATES_COURT', 'DESIGNATED', 'HIGHER'),
       ('PROJ8', 'Representation in the Magistrates Court - second claim for deferred sentence - category 1B - higher standard fee - designated area',  'FIXED', 'MAGISTRATES_COURT', 'DESIGNATED', 'HIGHER'),
       ('PROK1', 'Representation in the Magistrates Court - category 1A - lower standard fee - designated area', 'FIXED', 'MAGISTRATES_COURT', 'DESIGNATED', 'LOWER'),
       ('PROK2', 'Representation in the Magistrates Court - category 1B - lower standard fee - designated area', 'FIXED', 'MAGISTRATES_COURT', 'DESIGNATED', 'LOWER'),
       ('PROK3', 'Representation in the Magistrates Court - category 2 - lower standard fee - designated area','FIXED', 'MAGISTRATES_COURT', 'DESIGNATED', 'LOWER'),
       ('PROL1', 'Representation in the Magistrates Court - category 1A -  higher standard fee - designated area','FIXED', 'MAGISTRATES_COURT', 'DESIGNATED', 'HIGHER'),
       ('PROL2', 'Representation in the Magistrates Court - category 1B -  higher standard fee - designated area','FIXED', 'MAGISTRATES_COURT', 'DESIGNATED', 'HIGHER'),
       ('PROL3', 'Representation in the Magistrates Court - category 2 -  higher standard fee - designated area','FIXED', 'MAGISTRATES_COURT', 'DESIGNATED', 'HIGHER'),
       ('PROV1', 'Breach of part 1 injunctions under ASBCP Act - uncontested - lower standard fee', 'FIXED', 'MAGISTRATES_COURT', 'UNDESIGNATED', 'LOWER'),
       ('PROV2', 'Breach of part 1 injunctions under ASBCP Act - uncontested - higher standard fee', 'FIXED', 'MAGISTRATES_COURT', 'UNDESIGNATED', 'HIGHER'),
       ('PROV3', 'Breach of part 1 injunctions under ASBCP Act - contested - lower standard fee','FIXED', 'MAGISTRATES_COURT', 'UNDESIGNATED', 'LOWER'),
       ('PROV4', 'Breach of part 1 injunctions under ASBCP Act - contested - higher standard fee',  'FIXED', 'MAGISTRATES_COURT', 'UNDESIGNATED', 'HIGHER')
    ON CONFLICT (fee_code) DO NOTHING;

-- Youth court
INSERT INTO fee_code_information (fee_code, fee_description, fee_type, category_type, court_designation_type, fee_band_type)
VALUES ('YOUE1', 'Youth Representation Order - category 1A - lower standard fee - undesignated area',   'FIXED', 'YOUTH_COURT', 'UNDESIGNATED', 'LOWER'),
       ('YOUE2', 'Youth Representation Order - category 1B - lower standard fee - undesignated area',    'FIXED', 'YOUTH_COURT', 'UNDESIGNATED', 'LOWER'),
       ('YOUE3', 'Youth Representation Order - category 2A - lower standard fee - undesignated area',   'FIXED', 'YOUTH_COURT', 'UNDESIGNATED', 'LOWER'),
       ('YOUE4', 'Youth Representation Order - category 2B - lower standard fee - undesignated area',   'FIXED', 'YOUTH_COURT', 'UNDESIGNATED', 'LOWER'),
       ('YOUF1', 'Youth Representation Order - category 1A - higher standard fee - undesignated area',  'FIXED', 'YOUTH_COURT', 'UNDESIGNATED', 'HIGHER'),
       ('YOUF2', 'Youth Representation Order - category 1B - higher standard fee - undesignated area',   'FIXED', 'YOUTH_COURT', 'UNDESIGNATED', 'HIGHER'),
       ('YOUF3', 'Youth Representation Order - category 2A - higher standard fee - undesignated area',   'FIXED', 'YOUTH_COURT', 'UNDESIGNATED', 'HIGHER'),
       ('YOUF4', 'Youth Representation Order - category 2B - higher standard fee - undesignated area',  'FIXED', 'YOUTH_COURT', 'UNDESIGNATED', 'HIGHER'),
       ('YOUK1', 'Youth Representation Order - category 1A - lower standard fee - designated area',   'FIXED', 'YOUTH_COURT', 'DESIGNATED', 'LOWER'),
       ('YOUK2', 'Youth Representation Order - category 1B - lower standard fee - designated area',  'FIXED', 'YOUTH_COURT', 'DESIGNATED', 'LOWER'),
       ('YOUK3', 'Youth Representation Order - category 2A - lower standard fee - designated area', 'FIXED', 'YOUTH_COURT', 'DESIGNATED', 'LOWER'),
       ('YOUK4', 'Youth Representation Order - category 2B - lower standard fee - designated area',  'FIXED', 'YOUTH_COURT', 'DESIGNATED', 'LOWER'),
       ('YOUL1', 'Youth Representation Order - category 1A - higher standard fee - designated area', 'FIXED', 'YOUTH_COURT', 'DESIGNATED', 'HIGHER'),
       ('YOUL2', 'Youth Representation Order - category 1B - higher standard fee - designated area', 'FIXED', 'YOUTH_COURT', 'DESIGNATED', 'HIGHER'),
       ('YOUL3', 'Youth Representation Order - category 2A - higher standard fee - designated area',  'FIXED', 'YOUTH_COURT', 'DESIGNATED', 'HIGHER'),
       ('YOUL4', 'Youth Representation Order - category 2B - higher standard fee - designated area',   'FIXED', 'YOUTH_COURT', 'DESIGNATED', 'HIGHER'),
       ('YOUX1','Youth Representation Order - second claim for deferred sentence - category 1A - lower standard fee - undesignated area',   'FIXED', 'YOUTH_COURT', 'UNDESIGNATED', 'LOWER'),
       ('YOUX2','Youth Representation Order - second claim for deferred sentence - category 1B - lower standard fee - undesignated area',   'FIXED', 'YOUTH_COURT', 'UNDESIGNATED', 'LOWER'),
       ('YOUX3','Youth Representation Order - second claim for deferred sentence - category 1A - higher standard fee - undesignated area', 'FIXED', 'YOUTH_COURT', 'UNDESIGNATED', 'HIGHER'),
       ('YOUX4','Youth Representation Order - second claim for deferred sentence - category 1B - higher standard fee - undesignated area', 'FIXED', 'YOUTH_COURT', 'UNDESIGNATED', 'HIGHER'),
       ('YOUY1','Youth Representation Order - second claim for deferred sentence - category 1A - lower standard fee - designated area',   'FIXED', 'YOUTH_COURT', 'DESIGNATED', 'LOWER'),
       ('YOUY2','Youth Representation Order - second claim for deferred sentence - category 1B - lower standard fee - designated area',  'FIXED', 'YOUTH_COURT', 'DESIGNATED', 'LOWER'),
       ('YOUY3','Youth Representation Order - second claim for deferred sentence - category 1A - higher standard fee - designated area',  'FIXED', 'YOUTH_COURT', 'DESIGNATED', 'HIGHER'),
       ('YOUY4','Youth Representation Order - second claim for deferred sentence - category 1B - higher standard fee - designated area',  'FIXED', 'YOUTH_COURT', 'DESIGNATED', 'HIGHER')
    ON CONFLICT (fee_code) DO NOTHING;

-- prison law
INSERT INTO fee_code_information (fee_code, fee_description, fee_type, category_type, fee_band_type)
VALUES ('PRIA', 'Free Standing Advice and Assistance', 'FIXED', 'PRISON_LAW', 'STANDARD'),
       ('PRIB1', 'Disciplinary Cases – Advocacy Assistance - lower standard fee', 'FIXED', 'PRISON_LAW', 'LOWER'),
       ('PRIB2', 'Disciplinary Cases – Advocacy Assistance - higher standard fee', 'FIXED', 'PRISON_LAW', 'HIGHER'),
       ('PRIC1', 'Parole Board Cases – Advocacy Assistance - lower standard fee', 'FIXED', 'PRISON_LAW', 'LOWER'),
       ('PRIC2', 'Parole Board Cases – Advocacy Assistance - higher standard fee', 'FIXED', 'PRISON_LAW', 'HIGHER'),
       ('PRID1', 'Advocacy Assistance at Sentence Reviews - lower standard fee', 'FIXED', 'PRISON_LAW', 'LOWER'),
       ('PRID2', 'Advocacy Assistance at Sentence Reviews - higher standard fee', 'FIXED', 'PRISON_LAW', 'HIGHER'),
       ('PRIE1', 'Advocacy Assistance at Parole Board Reconsideration Hearings - lower standard fee', 'FIXED', 'PRISON_LAW', 'LOWER'),
       ('PRIE2', 'Advocacy Assistance at Parole Board Reconsideration Hearings - higher standard fee', 'FIXED', 'PRISON_LAW', 'HIGHER')
    ON CONFLICT (fee_code) DO NOTHING;

-- name
INSERT INTO fee_code_information (fee_code, fee_description, fee_type, category_type)
VALUES ('APPA', 'Advice and assistance in relation to an appeal (except CCRC)', 'FIXED', 'ADVOCACY_APPEALS_REVIEWS'),
       ('APPB', 'Advice and assistance in relation to CCRC application', 'FIXED', 'ADVOCACY_APPEALS_REVIEWS'),
       ('ASMS', 'Legal Help and Associated Civil Work – Miscellaneous', 'FIXED', 'ASSOCIATED_CIVIL'),
       ('ASPL', 'Legal Help and Associated Civil Work – Public Law', 'FIXED', 'ASSOCIATED_CIVIL'),
       ('ASAS', 'Part 1 injunction Anti-Social Behaviour Crime and Policing Act 2014', 'FIXED', 'ASSOCIATED_CIVIL')
    ON CONFLICT (fee_code) DO NOTHING;

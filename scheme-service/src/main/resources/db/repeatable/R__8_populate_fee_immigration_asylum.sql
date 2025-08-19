-- 2013 SCHEME (I&A_FS2013)
INSERT INTO fee
(fee_code, description, fixed_fee, escape_threshold_limit, disbursement_limit,fee_scheme_code, calculation_type, ho_interview_bolt_on, adjorn_hearing_bolt_on, oral_cmrh_bolt_on, telephone_cmrh_bolt_on)
VALUES ('IACA', 'Asylum CLR IMMIGRATION_ASYLUM Fee 2a', 227, 681, 600, 'I&A_FS2013', 'IMMIGRATION_ASYLUM', NULL, NULL, 166, 90),
       ('IACB', 'Asylum CLR IMMIGRATION_ASYLUM Fee (2b + advocacy substantive hearing fee)', 869, 1701, 600, 'I&A_FS2013', 'IMMIGRATION_ASYLUM', NULL, 161, 166, 90),
       ('IALB', 'Asylum LH IMMIGRATION_ASYLUM Fee', 413, 1239, 400, 'I&A_FS2013', 'IMMIGRATION_ASYLUM', 266, NULL, NULL, NULL),
       ('IMCA', 'Immigration CLR IMMIGRATION_ASYLUM Fee 2a', 227, 681, 600, 'I&A_FS2013', 'IMMIGRATION_ASYLUM', NULL, NULL, 166, 90),
       ('IMCB', 'Immigration CLR IMMIGRATION_ASYLUM Fee (2b + advocacy substantive hearing fee)', 691, 1362, 600, 'I&A_FS2013', 'IMMIGRATION_ASYLUM', NULL, 161, 166, 90),
       ('IMLB', 'Immigration LH IMMIGRATION_ASYLUM Fee', 234, 702, 400, 'I&A_FS2013', 'IMMIGRATION_ASYLUM', 266, NULL, NULL, NULL),
       ('IDAS1', 'Detained Duty Advice Scheme (1-4 clients seen)', 180, NULL, NULL, 'I&A_FS2013', 'IMMIGRATION_ASYLUM', NULL, NULL, NULL, NULL),
       ('IDAS2', 'Detained Duty Advice Scheme (5+ clients seen)', 360, NULL, NULL, 'I&A_FS2013', 'IMMIGRATION_ASYLUM', NULL, NULL, NULL, NULL)
ON CONFLICT (fee_code, fee_scheme_code) DO NOTHING;

-- 2020 SCHEME (I&A_FS2020)
INSERT INTO fee
(fee_code, description, fixed_fee, escape_threshold_limit, disbursement_limit,fee_scheme_code, calculation_type, ho_interview_bolt_on, adjorn_hearing_bolt_on, oral_cmrh_bolt_on, telephone_cmrh_bolt_on)
VALUES ('IACA', 'Asylum CLR IMMIGRATION_ASYLUM Fee 2a', 227, 681, 600, 'I&A_FS2020', 'IMMIGRATION_ASYLUM', NULL, NULL, 166, 90),
       ('IACB', 'Asylum CLR IMMIGRATION_ASYLUM Fee (2b + advocacy substantive hearing fee)', 869, 1701, 600, 'I&A_FS2020', 'IMMIGRATION_ASYLUM', NULL, 161, 166, 90),
       ('IACC', 'Asylum CLR (2c + advocacy substantive hearing fee)', 929, 1881, 600, 'I&A_FS2020', 'IMMIGRATION_ASYLUM', NULL, 161, 166, 90),
       ('IALB', 'Asylum LH IMMIGRATION_ASYLUM Fee', 413, 1239, 400, 'I&A_FS2020', 'IMMIGRATION_ASYLUM', 266, NULL, NULL, NULL),
       ('IMCA', 'Immigration CLR IMMIGRATION_ASYLUM Fee 2a', 227, 681, 600, 'I&A_FS2020', 'IMMIGRATION_ASYLUM', NULL, NULL, 166, 90),
       ('IMCB', 'Immigration CLR IMMIGRATION_ASYLUM Fee (2b + advocacy substantive hearing fee)', 691, 1362, 600, 'I&A_FS2020', 'IMMIGRATION_ASYLUM', NULL, 161, 166, 90),
       ('IMCC', 'Standard Fee - Immigration CLR (2c + advocacy substantive hearing fee)', 764, 1581, 600, 'I&A_FS2020', 'IMMIGRATION_ASYLUM', NULL, 161, 166, 90),
       ('IMLB', 'Immigration LH IMMIGRATION_ASYLUM Fee', 234, 702, 400, 'I&A_FS2020', 'IMMIGRATION_ASYLUM', 266, NULL, NULL, NULL),
       ('IDAS1', 'Detained Duty Advice Scheme (1-4 clients seen)', 180, NULL, NULL, 'I&A_FS2020', 'IMMIGRATION_ASYLUM', NULL, NULL, NULL, NULL),
       ('IDAS2', 'Detained Duty Advice Scheme (5+ clients seen)', 360, NULL, NULL, 'I&A_FS2020', 'IMMIGRATION_ASYLUM', NULL, NULL, NULL, NULL)
ON CONFLICT (fee_code, fee_scheme_code) DO NOTHING;

-- 2023 SCHEME (I&A_FS2023)
INSERT INTO fee
(fee_code, description, fixed_fee, escape_threshold_limit, disbursement_limit,fee_scheme_code, calculation_type, ho_interview_bolt_on, adjorn_hearing_bolt_on, oral_cmrh_bolt_on, telephone_cmrh_bolt_on)
VALUES ('IACE', 'Asylum CLR IMMIGRATION_ASYLUM Fee 2d', 669, 1338, 600, 'I&A_FS2023', 'IMMIGRATION_ASYLUM', NULL, NULL, 166, 90),
       ('IACF', 'Asylum CLR IMMIGRATION_ASYLUM Fee (2e + advocacy substantive hearing fee)', 1311, 2018, 600, 'I&A_FS2023', 'IMMIGRATION_ASYLUM', NULL, 161, 166, 90),
       ('IALB', 'Asylum LH IMMIGRATION_ASYLUM Fee', 413, 826, 400, 'I&A_FS2023', 'IMMIGRATION_ASYLUM', 266, NULL, NULL, NULL),
       ('IMCE', 'Immigration CLR IMMIGRATION_ASYLUM Fee 2d', 628, 1256, 600, 'I&A_FS2023', 'IMMIGRATION_ASYLUM', NULL, NULL, 166, 90),
       ('IMCF', 'Immigration CLR IMMIGRATION_ASYLUM Fee (2e + advocacy substantive hearing fee)', 1092, 1710, 600, 'I&A_FS2023', 'IMMIGRATION_ASYLUM', NULL, 161, 166, 90),
       ('IMLB', 'Immigration LH IMMIGRATION_ASYLUM Fee', 234, 468, 400, 'I&A_FS2023', 'IMMIGRATION_ASYLUM', 266, NULL, NULL, NULL),
       ('IDAS1', 'Detained Duty Advice Scheme (1-4 clients seen)', 180, NULL, NULL, 'I&A_FS2023', 'IMMIGRATION_ASYLUM', NULL, NULL, NULL, NULL),
       ('IDAS2', 'Detained Duty Advice Scheme (5+ clients seen)', 360, NULL, NULL, 'I&A_FS2023', 'IMMIGRATION_ASYLUM', NULL, NULL, NULL, NULL)
ON CONFLICT (fee_code, fee_scheme_code) DO NOTHING;

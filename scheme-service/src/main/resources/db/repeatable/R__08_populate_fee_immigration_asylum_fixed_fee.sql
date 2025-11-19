-- 2013 SCHEME (IMM_ASYLM_FS2013)
INSERT INTO fee
(fee_code, fixed_fee, escape_threshold_limit, disbursement_limit, fee_scheme_code, ho_interview_bolt_on, adjorn_hearing_bolt_on, oral_cmrh_bolt_on, telephone_cmrh_bolt_on, substantive_hearing_bolt_on)
VALUES ('IACA', 227, 681, 6, 'IMM_ASYLM_FS2013', NULL, NULL, 166, 90, NULL),
       ('IACB', 869, 1701, 600, 'IMM_ASYLM_FS2013', NULL, 161, 166, 90, 302),
       ('IALB', 413, 1239, 400, 'IMM_ASYLM_FS2013', 266, NULL, NULL, NULL, NULL),
       ('IMCA',  227, 681, 600, 'IMM_ASYLM_FS2013', NULL, NULL, 166, 90, NULL),
       ('IMCB',  691, 1362, 600, 'IMM_ASYLM_FS2013', NULL, 161, 166, 90, 237),
       ('IMLB', 234, 702, 400, 'IMM_ASYLM_FS2013', 266, NULL, NULL, NULL, NULL),
       ('IDAS1', 180, NULL, NULL, 'IMM_ASYLM_FS2013', NULL, NULL, NULL, NULL, NULL),
       ('IDAS2', 360, NULL, NULL, 'IMM_ASYLM_FS2013', NULL, NULL, NULL, NULL, NULL)
ON CONFLICT (fee_code, fee_scheme_code) DO NOTHING;

-- 2020 SCHEME (IMM_ASYLM_FS2020)
INSERT INTO fee
(fee_code, fixed_fee, escape_threshold_limit, disbursement_limit, fee_scheme_code, ho_interview_bolt_on, adjorn_hearing_bolt_on, oral_cmrh_bolt_on, telephone_cmrh_bolt_on, substantive_hearing_bolt_on)
VALUES ('IACA', 227, 681, 600, 'IMM_ASYLM_FS2020', NULL, NULL, 166, 90, 302),
       ('IACB',  869, 1701, 600, 'IMM_ASYLM_FS2020', NULL, 161, 166, 90, 302),
       ('IACC',  929, 1881, 600, 'IMM_ASYLM_FS2020', NULL, 161, 166, 90, 302),
       ('IALB', 413, 1239, 400, 'IMM_ASYLM_FS2020', 266, NULL, NULL, NULL, NULL),
       ('IMCA', 227, 681, 600, 'IMM_ASYLM_FS2020', NULL, NULL, 166, 90, NULL),
       ('IMCB', 691, 1362, 600, 'IMM_ASYLM_FS2020', NULL, 161, 166, 90, 237),
       ('IMCC', 764, 1581, 600, 'IMM_ASYLM_FS2020', NULL, 161, 166, 90, 237),
       ('IMLB', 234, 702, 400, 'IMM_ASYLM_FS2020', 266, NULL, NULL, NULL, NULL),
       ('IDAS1', 180, NULL, NULL, 'IMM_ASYLM_FS2020', NULL, NULL, NULL, NULL, NULL),
       ('IDAS2', 360, NULL, NULL, 'IMM_ASYLM_FS2020', NULL, NULL, NULL, NULL, NULL)
ON CONFLICT (fee_code, fee_scheme_code) DO NOTHING;

-- 2023 SCHEME (IMM_ASYLM_FS2023)
INSERT INTO fee
(fee_code, fixed_fee, escape_threshold_limit, disbursement_limit,fee_scheme_code, ho_interview_bolt_on, adjorn_hearing_bolt_on, oral_cmrh_bolt_on, telephone_cmrh_bolt_on, substantive_hearing_bolt_on)
VALUES ('IACE', 669, 1338, 600, 'IMM_ASYLM_FS2023', NULL, NULL, 166, 90, NULL),
       ('IACF', 1311, 2018, 600, 'IMM_ASYLM_FS2023', NULL, 161, 166, 90, 302),
       ('IALB', 413, 826, 400, 'IMM_ASYLM_FS2023', 266, NULL, NULL, NULL, NULL),
       ('IMCE', 628, 1256, 600, 'IMM_ASYLM_FS2023', NULL, NULL, 166, 90, NULL),
       ('IMCF', 1092, 1710, 600, 'IMM_ASYLM_FS2023', NULL, 161, 166, 90, 237),
       ('IMLB', 234, 468, 400, 'IMM_ASYLM_FS2023', 266, NULL, NULL, NULL, NULL),
       ('IDAS1', 180, NULL, NULL, 'IMM_ASYLM_FS2023', NULL, NULL, NULL, NULL, NULL),
       ('IDAS2', 360, NULL, NULL, 'IMM_ASYLM_FS2023', NULL, NULL, NULL, NULL, NULL)
ON CONFLICT (fee_code, fee_scheme_code) DO NOTHING;

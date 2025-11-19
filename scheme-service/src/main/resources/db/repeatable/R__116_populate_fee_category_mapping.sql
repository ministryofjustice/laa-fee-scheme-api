-- Block 1
INSERT INTO fee_category_mapping (fee_id, fee_scheme_category_type_id, category_of_law_type_id)
SELECT f.fee_id, 1, 11
FROM fee f
WHERE f.fee_code IN (
                     'IACA','IACB','IACC','IACE','IACF','IALB','IMCA','IMCB','IMCC','IMCE','IMCF','IMLB',
                     'IDAS1','IDAS2','IAXL','IMXL','IA100','IAXC','IMXC','IRAR','IACD','IMCD','ICASD','ICISD','ICSSD','ILHSD'
    )
    ON CONFLICT (fee_id) DO NOTHING;

-- Block 2
INSERT INTO fee_category_mapping (fee_id, fee_scheme_category_type_id, category_of_law_type_id)
SELECT f.fee_id, 2, 21
FROM fee f
WHERE f.fee_code IN ('MHL01','MHL02','MHL03','MHL04','MHL05','MHL06','MHL07','MHL08','MHL10','MHLDIS')
    ON CONFLICT (fee_id) DO NOTHING;

-- Block 3
INSERT INTO fee_category_mapping (fee_id, fee_scheme_category_type_id, category_of_law_type_id)
SELECT f.fee_id, 3, 7
FROM fee f
WHERE f.fee_code IN (
                     'FPB010','FPB020','FPB030','FVP100','FVP012','FVP011','FVP013','FVP010','FVP110','FVP130','FVP120','FVP140','FVP150',
                     'FVP180','FVP160','FVP170','FVP190','FVP200','FVP210','FVP020','FVP040','FVP030','FVP050','FVP060','FVP090','FVP070',
                     'FVP080','FVP021','FVP041','FVP031','FVP051','FVP061','FVP091','FVP071','FVP081'
    )
    ON CONFLICT (fee_id) DO NOTHING;

-- Block 4
INSERT INTO fee_category_mapping (fee_id, fee_scheme_category_type_id, category_of_law_type_id)
SELECT f.fee_id, 4, 6
FROM fee f
WHERE f.fee_code IN (
                     'ASSA','ASSS','ASST','MDAS2B','MDAS1B','MDAC2B','MDAC1B','MDAS2S','MDAS1S','MDAS2P','MDAS1P','MDAS2C','MDAS1C',
                     'MDAC2S','MDAC1S','MDAC2P','MDAC1P','MDAC2C','MDAC1C','MDPS2B','MDPS1B','MDPC2B','MDPC1B','MDPS2S','MDPS1S','MDPC2S',
                     'MDPC1S','MDCS2B','MDCS1B','MDCC2B','MDCC1B','MDCS2S','MDCS1S','MDCC2S','MDCC1S'
    )
    ON CONFLICT (fee_id) DO NOTHING;

-- Block 5
INSERT INTO fee_category_mapping (fee_id, fee_scheme_category_type_id, category_of_law_type_id)
SELECT f.fee_id, 5, 18
FROM fee f
WHERE f.fee_code = 'COM'
    ON CONFLICT (fee_id) DO NOTHING;

INSERT INTO fee_category_mapping (fee_id, fee_scheme_category_type_id, category_of_law_type_id)
SELECT f.fee_id, 6, 15
FROM fee f
WHERE f.fee_code = 'CAPA'
    ON CONFLICT (fee_id) DO NOTHING;

INSERT INTO fee_category_mapping (fee_id, fee_scheme_category_type_id, category_of_law_type_id)
SELECT f.fee_id, 7, 10
FROM fee f
WHERE f.fee_code = 'CLIN'
    ON CONFLICT (fee_id) DO NOTHING;

INSERT INTO fee_category_mapping (fee_id, fee_scheme_category_type_id, category_of_law_type_id)
SELECT f.fee_id, 8, 19
FROM fee f
WHERE f.fee_code = 'DEBT'
    ON CONFLICT (fee_id) DO NOTHING;

INSERT INTO fee_category_mapping (fee_id, fee_scheme_category_type_id, category_of_law_type_id)
SELECT f.fee_id, 9, 8
FROM fee f
WHERE f.fee_code = 'DISC'
    ON CONFLICT (fee_id) DO NOTHING;

INSERT INTO fee_category_mapping (fee_id, fee_scheme_category_type_id, category_of_law_type_id)
SELECT f.fee_id, 10, 17
FROM fee f
WHERE f.fee_code IN ('EDUFIN','EDUDIS')
    ON CONFLICT (fee_id) DO NOTHING;

INSERT INTO fee_category_mapping (fee_id, fee_scheme_category_type_id, category_of_law_type_id)
SELECT f.fee_id, 11, 14
FROM fee f
WHERE f.fee_code = 'ELA'
    ON CONFLICT (fee_id) DO NOTHING;

INSERT INTO fee_category_mapping (fee_id, fee_scheme_category_type_id, category_of_law_type_id)
SELECT f.fee_id, 12, 13
FROM fee f
WHERE f.fee_code = 'HOUS'
    ON CONFLICT (fee_id) DO NOTHING;

INSERT INTO fee_category_mapping (fee_id, fee_scheme_category_type_id, category_of_law_type_id)
SELECT f.fee_id, 13, 23
FROM fee f
WHERE f.fee_code IN ('MISCGEN','MISCCON','MISCPI','MISCASBI','MISCEMP')
    ON CONFLICT (fee_id) DO NOTHING;

INSERT INTO fee_category_mapping (fee_id, fee_scheme_category_type_id, category_of_law_type_id)
SELECT f.fee_id, 14, 9
FROM fee f
WHERE f.fee_code = 'PUB'
    ON CONFLICT (fee_id) DO NOTHING;

INSERT INTO fee_category_mapping (fee_id, fee_scheme_category_type_id, category_of_law_type_id)
SELECT f.fee_id, 15, 16
FROM fee f
WHERE f.fee_code = 'WFB1'
    ON CONFLICT (fee_id) DO NOTHING;

-- Block 6
INSERT INTO fee_category_mapping (fee_id, fee_scheme_category_type_id, category_of_law_type_id)
SELECT f.fee_id, 16, 3
FROM fee f
WHERE f.fee_code = 'INVC'
    ON CONFLICT (fee_id) DO NOTHING;

INSERT INTO fee_category_mapping (fee_id, fee_scheme_category_type_id, category_of_law_type_id)
SELECT f.fee_id, 17, 3
FROM fee f
WHERE f.fee_code IN ('INVA','INVE','INVH','INVK','INVL','INVM','INVB1','INVB2')
    ON CONFLICT (fee_id) DO NOTHING;

-- Block 7
INSERT INTO fee_category_mapping (fee_id, fee_scheme_category_type_id, category_of_law_type_id)
SELECT f.fee_id, 18, 3
FROM fee f
WHERE f.fee_code IN (
                     'PROE1','PROE2','PROE3','PROF1','PROF2','PROF3','PROJ1','PROJ2','PROJ3','PROJ4','PROJ5','PROJ6','PROJ7','PROJ8',
                     'PROK1','PROK2','PROK3','PROL1','PROL2','PROL3','PROV1','PROV2','PROV3','PROV4','PROT','PROU','PROW','PROD',
                     'PROP1','PROP2','PROH','YOUE1','YOUE2','YOUE3','YOUE4','YOUF1','YOUF2','YOUF3','YOUF4','YOUK1','YOUK2','YOUK3',
                     'YOUK4','YOUL1','YOUL2','YOUL3','YOUL4','YOUX1','YOUX2','YOUX3','YOUX4','YOUY1','YOUY2','YOUY3','YOUY4'
    )
    ON CONFLICT (fee_id) DO NOTHING;

-- Block 8
INSERT INTO fee_category_mapping (fee_id, fee_scheme_category_type_id, category_of_law_type_id)
SELECT f.fee_id, 19, 4
FROM fee f
WHERE f.fee_code IN ('PRIA','PRIB1','PRIB2','PRIC1','PRIC2','PRID1','PRID2','PRIE1','PRIE2')
    ON CONFLICT (fee_id) DO NOTHING;

INSERT INTO fee_category_mapping (fee_id, fee_scheme_category_type_id, category_of_law_type_id)
SELECT f.fee_id, 20, 5
FROM fee f
WHERE f.fee_code IN ('APPA','APPB')
    ON CONFLICT (fee_id) DO NOTHING;

INSERT INTO fee_category_mapping (fee_id, fee_scheme_category_type_id, category_of_law_type_id)
SELECT f.fee_id, 21, 2
FROM fee f
WHERE f.fee_code IN ('ASMS','ASPL','ASAS')
    ON CONFLICT (fee_id) DO NOTHING;

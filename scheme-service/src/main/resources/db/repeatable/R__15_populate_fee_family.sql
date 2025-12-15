INSERT INTO fee
(fee_code, fixed_fee, escape_threshold_limit, region, fee_scheme_code)
VALUES ('FPB010', 132, 396, 'LONDON', 'FAM_LON_FS2013'),
       ('FPB010', 132, 396, 'NON_LONDON', 'FAM_NON_LON_FS2013'),
       ('FPB020', 365, 1095, 'LONDON', 'FAM_LON_FS2013'),
       ('FPB020', 365, 1095, 'NON_LONDON', 'FAM_NON_LON_FS2013'),
       ('FPB030', 497, 1491, 'LONDON', 'FAM_LON_FS2013'),
       ('FPB030', 497, 1491, 'NON_LONDON', 'FAM_NON_LON_FS2013'),
       ('FVP100', 146, 438, 'LONDON', 'FAM_LON_FS2013'),
       ('FVP100', 146, 438, 'NON_LONDON', 'FAM_NON_LON_FS2013'),
       ('FVP012', 86, NULL, 'LONDON', 'FAM_LON_FS2013'),
       ('FVP012', 86, NULL, 'NON_LONDON', 'FAM_NON_LON_FS2013')
ON CONFLICT (fee_code, fee_scheme_code) DO NOTHING;

INSERT INTO fee
(fee_code, fixed_fee, escape_threshold_limit, region, fee_scheme_code)
VALUES ('FVP011', 86, 258, 'LONDON', 'FAM_LON_FS2013'),
       ('FVP011', 86, 258, 'NON_LONDON', 'FAM_NON_LON_FS2013'),
       ('FVP013', 86, 258, 'LONDON', 'FAM_LON_FS2013'),
       ('FVP013', 86, 258, 'NON_LONDON', 'FAM_NON_LON_FS2013'),
       ('FVP010', 86, NULL, 'LONDON', 'FAM_LON_FS2013'),
       ('FVP010', 86, NULL, 'NON_LONDON', 'FAM_NON_LON_FS2013'),
       ('FVP110', 368, 690, 'LONDON', 'FAM_LON_FS2013'),
       ('FVP110', 318, 597, 'NON_LONDON', 'FAM_NON_LON_FS2013'),
       ('FVP130', 230, 690, 'LONDON', 'FAM_LON_FS2013'),
       ('FVP130', 199, 597, 'NON_LONDON', 'FAM_NON_LON_FS2013')
ON CONFLICT (fee_code, fee_scheme_code) DO NOTHING;

INSERT INTO fee
(fee_code, fixed_fee, escape_threshold_limit, region, fee_scheme_code)
VALUES ('FVP120', 386, 723, 'LONDON', 'FAM_LON_FS2013'),
       ('FVP120', 333, 624, 'NON_LONDON', 'FAM_NON_LON_FS2013'),
       ('FVP140', 241, 723, 'LONDON', 'FAM_LON_FS2013'),
       ('FVP140', 208, 624, 'NON_LONDON', 'FAM_NON_LON_FS2013'),
       ('FVP150', 754, 1413, 'LONDON', 'FAM_LON_FS2013'),
       ('FVP150', 651, 1221, 'NON_LONDON', 'FAM_NON_LON_FS2013'),
       ('FVP180', 471, 1413, 'LONDON', 'FAM_LON_FS2013'),
       ('FVP180', 407, 1221, 'NON_LONDON', 'FAM_NON_LON_FS2013'),
       ('FVP160', 609, 1413, 'LONDON', 'FAM_LON_FS2013'),
       ('FVP160', 526, 1221, 'NON_LONDON', 'FAM_NON_LON_FS2013')
ON CONFLICT (fee_code, fee_scheme_code) DO NOTHING;

INSERT INTO fee
(fee_code, fixed_fee, escape_threshold_limit, region, fee_scheme_code)
VALUES ('FVP170', 616, 1413, 'LONDON', 'FAM_LON_FS2013'),
       ('FVP170', 532, 1221, 'NON_LONDON', 'FAM_NON_LON_FS2013'),
       ('FVP190', 150, NULL, 'LONDON', 'FAM_LON_FS2013'),
       ('FVP190', 150, NULL, 'NON_LONDON', 'FAM_NON_LON_FS2013'),
       ('FVP200', 200, NULL, 'LONDON', 'FAM_LON_FS2013'),
       ('FVP200', 200, NULL, 'NON_LONDON', 'FAM_NON_LON_FS2013'),
       ('FVP210', 350, NULL, 'LONDON', 'FAM_LON_FS2013'),
       ('FVP210', 350, NULL, 'NON_LONDON', 'FAM_NON_LON_FS2013'),
       ('FVP020', 454, 948, 'LONDON', 'FAM_LON_FS2013'),
       ('FVP020', 404, 855, 'NON_LONDON', 'FAM_NON_LON_FS2013')
ON CONFLICT (fee_code, fee_scheme_code) DO NOTHING;

INSERT INTO fee
(fee_code, fixed_fee, escape_threshold_limit, region, fee_scheme_code)
VALUES ('FVP040', 316, 948, 'LONDON', 'FAM_LON_FS2013'),
       ('FVP040', 285, 855, 'NON_LONDON', 'FAM_NON_LON_FS2013'),
       ('FVP030', 472, 981, 'LONDON', 'FAM_LON_FS2013'),
       ('FVP030', 419, 882, 'NON_LONDON', 'FAM_NON_LON_FS2013'),
       ('FVP050', 327, 981, 'LONDON', 'FAM_LON_FS2013'),
       ('FVP050', 294, 882, 'NON_LONDON', 'FAM_NON_LON_FS2013'),
       ('FVP060', 840, 1671, 'LONDON', 'FAM_LON_FS2013'),
       ('FVP060', 737, 1479, 'NON_LONDON', 'FAM_NON_LON_FS2013'),
       ('FVP090', 557, 1671, 'LONDON', 'FAM_LON_FS2013'),
       ('FVP090', 493, 1479, 'NON_LONDON', 'FAM_NON_LON_FS2013')
ON CONFLICT (fee_code, fee_scheme_code) DO NOTHING;

INSERT INTO fee
(fee_code, fixed_fee, escape_threshold_limit, region, fee_scheme_code)
VALUES ('FVP070', 695, 1671, 'LONDON', 'FAM_LON_FS2013'),
       ('FVP070', 612, 1479, 'NON_LONDON', 'FAM_NON_LON_FS2013'),
       ('FVP080', 702, 1671, 'LONDON', 'FAM_LON_FS2013'),
       ('FVP080', 618, 1479, 'NON_LONDON', 'FAM_NON_LON_FS2013'),
       ('FVP021', 454, 948, 'LONDON', 'FAM_LON_FS2013'),
       ('FVP021', 404, 855, 'NON_LONDON', 'FAM_NON_LON_FS2013'),
       ('FVP041', 316, 948, 'LONDON', 'FAM_LON_FS2013'),
       ('FVP041', 285, 855, 'NON_LONDON', 'FAM_NON_LON_FS2013'),
       ('FVP031', 472, 981, 'LONDON', 'FAM_LON_FS2013'),
       ('FVP031', 419, 882, 'NON_LONDON', 'FAM_NON_LON_FS2013')
ON CONFLICT (fee_code, fee_scheme_code) DO NOTHING;

INSERT INTO fee
(fee_code, fixed_fee, escape_threshold_limit, region, fee_scheme_code)
VALUES ('FVP051', 327, 981, 'LONDON', 'FAM_LON_FS2013'),
       ('FVP051', 294, 882, 'NON_LONDON', 'FAM_NON_LON_FS2013'),
       ('FVP061', 840, 1671, 'LONDON', 'FAM_LON_FS2013'),
       ('FVP061', 737, 1479, 'NON_LONDON', 'FAM_NON_LON_FS2013'),
       ('FVP091', 557, 1671, 'LONDON', 'FAM_LON_FS2013'),
       ('FVP091', 493, 1479, 'NON_LONDON', 'FAM_NON_LON_FS2013'),
       ('FVP071', 695, 1671, 'LONDON', 'FAM_LON_FS2013'),
       ('FVP071', 612, 1479, 'NON_LONDON', 'FAM_NON_LON_FS2013'),
       ('FVP081', 702, 1671, 'LONDON', 'FAM_LON_FS2013'),
       ('FVP081', 618, 1479, 'NON_LONDON', 'FAM_NON_LON_FS2013')
ON CONFLICT (fee_code, fee_scheme_code) DO NOTHING;

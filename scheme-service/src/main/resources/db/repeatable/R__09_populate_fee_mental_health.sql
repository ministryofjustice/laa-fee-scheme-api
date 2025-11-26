INSERT INTO fee (fee_code, fixed_fee, escape_threshold_limit, adjorn_hearing_bolt_on, fee_scheme_code)
VALUES ('MHL01', 263.00, 759.00, NULL, 'MHL_FS2013'),
       ('MHL02', 129.00, 387.00, 117.00,'MHL_FS2013'),
       ('MHL03', 450.00, 1350.00, 117.00,'MHL_FS2013'),
       ('MHL04', 744.00, 2232.00, 117.00, 'MHL_FS2013'),
       ('MHL05', 321.00, 963.00, 117.00,'MHL_FS2013'),
       ('MHL06', 615.00, 1845.00, 117.00, 'MHL_FS2013'),
       ('MHL07', 294.00, 882.00, 117.00, 'MHL_FS2013'),
       ('MHL08', 423.00, 1269.00, 117.00, 'MHL_FS2013'),
       ('MHL10', 129.00, NULL,NULL, 'MHL_FS2013'),
       ('MHLDIS', NULL, NULL,NULL, 'MHL_DISB_FS2020')
ON CONFLICT (fee_code, fee_scheme_code) DO NOTHING;
INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Hartlepool', '1001', 131.40, 405.40, 'POL_FS2016'),
       ('Teeside', '1002', 135.96, 417.03, 'POL_FS2016'),
       ('Darlington', '1003', 154.54, 463.62, 'POL_FS2016'),
       ('South Durham', '1004', 152.39, 468.28, 'POL_FS2016'),
       ('Durham', '1005', 177.94, 554.48, 'POL_FS2016'),
       ('Derwentside', '1006', 171.63, 514.90, 'POL_FS2016'),
       ('Easington', '1007', 166.99, 512.54, 'POL_FS2016'),
       ('South East Northumberland', '1008', 148.33, 444.98, 'POL_FS2016'),
       ('Newcastle upon Tyne', '1009', 137.79, 424.01, 'POL_FS2016'),
       ('Gateshead', '1010', 142.90, 428.69, 'POL_FS2016')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('North Tyneside', '1011', 140.53, 431.02, 'POL_FS2016'),
       ('South Tyneside', '1012', 133.23, 410.05, 'POL_FS2016'),
       ('Sunderland / Houghton Le Spring', '1013', 148.74, 458.97, 'POL_FS2016'),
       ('Berwick & Alnwick', '1014', 177.03, 545.17, 'POL_FS2016'),
       ('Tynedale & Hexham', '1015', 154.21, 475.28, 'POL_FS2016'),
       ('Abingdon, Didcot & Witney (South Oxfordshire)', '1131', 208.96, 708.25, 'POL_FS2016'),
       ('Aylesbury', '1132', 198.81, 596.42, 'POL_FS2016'),
       ('High Wycombe & Amersham', '1133', 190.71, 647.69, 'POL_FS2016'),
       ('Milton Keynes', '1134', 165.16, 507.89, 'POL_FS2016'),
       ('Bicester / North Oxon (Banbury)', '1135', 194.36, 659.33, 'POL_FS2016')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Oxford', '1136', 194.36, 659.33, 'POL_FS2016'),
       ('Reading', '1137', 188.71, 566.14, 'POL_FS2016'),
       ('Slough (East Berkshire)', '1138', 208.96, 708.25, 'POL_FS2016'),
       ('West Berkshire (Newbury Etc)', '1139', 174.73, 524.20, 'POL_FS2016'),
       ('Aldershot / Petersfield (North East Hampshire)', '1140', 199.84, 677.97, 'POL_FS2016'),
       ('Andover / Basingstoke / Winchester (NW Hants)', '1141', 210.46, 631.38, 'POL_FS2016'),
       ('Isle Of Wight', '1142', 171.63, 514.90, 'POL_FS2016'),
       ('Portsmouth / Waterlooville (South East Hampshire)', '1143', 176.29, 528.86, 'POL_FS2016'),
       ('Gosport & Fareham', '1144', 215.11, 645.34, 'POL_FS2016'),
       ('Southampton (South West Hampshire)', '1145', 198.81, 596.42, 'POL_FS2016')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Grimsby & Cleethorpes', '1201', 134.35, 403.04, 'POL_FS2016'),
       ('Scunthorpe', '1202', 144.18, 444.98, 'POL_FS2016'),
       ('Hull', '1203', 153.30, 470.60, 'POL_FS2016'),
       ('Beverley / Bridlington', '1204', 177.94, 587.11, 'POL_FS2016'),
       ('Goole', '1205', 182.50, 617.39, 'POL_FS2016'),
       ('Northallerton & Richmond', '1206', 191.82, 575.45, 'POL_FS2016'),
       ('Harrogate & Ripon', '1207', 184.05, 552.15, 'POL_FS2016'),
       ('Skipton, Settle & Ingleton', '1208', 177.94, 547.50, 'POL_FS2016'),
       ('Scarborough / Whitby', '1209', 152.39, 468.28, 'POL_FS2016'),
       ('Malton & Ryedale', '1210', 146.78, 440.33, 'POL_FS2016')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('York / Selby', '1211', 159.69, 491.57, 'POL_FS2016'),
       ('Barnsley', '1212', 158.78, 489.25, 'POL_FS2016'),
       ('Doncaster', '1213', 153.30, 470.60, 'POL_FS2016'),
       ('Rotherham', '1214', 162.43, 500.91, 'POL_FS2016'),
       ('Sheffield', '1215', 166.99, 514.90, 'POL_FS2016'),
       ('Halifax', '1216', 173.96, 521.88, 'POL_FS2016'),
       ('Huddersfield', '1217', 146.78, 440.33, 'POL_FS2016'),
       ('Dewsbury', '1218', 159.20, 477.61, 'POL_FS2016'),
       ('Bradford', '1219', 135.96, 419.36, 'POL_FS2016'),
       ('Keighley & Bingley', '1220', 153.30, 470.60, 'POL_FS2016')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Leeds', '1221', 144.18, 442.65, 'POL_FS2016'),
       ('Pontefract & Castleford', '1222', 141.34, 424.01, 'POL_FS2016'),
       ('Wakefield', '1223', 139.61, 428.69, 'POL_FS2016'),
       ('Barking', '1301', 224.48, 761.85, 'POL_FS2016'),
       ('Bexley', '1302', 200.75, 680.30, 'POL_FS2016'),
       ('Bishopsgate', '1303', 234.51, 794.45, 'POL_FS2016'),
       ('Brent', '1304', 219.00, 740.88, 'POL_FS2016'),
       ('Brentford', '1305', 222.65, 754.84, 'POL_FS2016'),
       ('Bromley', '1306', 211.70, 717.58, 'POL_FS2016'),
       ('Camberwell Green', '1307', 219.00, 743.20, 'POL_FS2016')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Central London', '1308', 237.25, 803.78, 'POL_FS2016'),
       ('Clerkenwell/Hampstead', '1309', 221.74, 750.18, 'POL_FS2016'),
       ('Croydon', '1310', 216.26, 731.54, 'POL_FS2016'),
       ('Ealing', '1311', 229.95, 780.67, 'POL_FS2016'),
       ('Enfield', '1312', 218.09, 738.55, 'POL_FS2016'),
       ('Greenwich/Woolwich', '1313', 208.96, 708.25, 'POL_FS2016'),
       ('Haringey', '1314', 225.39, 764.17, 'POL_FS2016'),
       ('Harrow', '1315', 219.00, 743.20, 'POL_FS2016'),
       ('Havering', '1316', 204.40, 691.96, 'POL_FS2016'),
       ('Heathrow', '1317', 274.66, 931.93, 'POL_FS2016')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Hendon/Barnet', '1318', 220.83, 747.86, 'POL_FS2016'),
       ('Highbury Corner', '1319', 229.95, 778.16, 'POL_FS2016'),
       ('Kingston-Upon-Thames', '1320', 228.13, 773.48, 'POL_FS2016'),
       ('Newham', '1321', 219.91, 745.53, 'POL_FS2016'),
       ('Old Street', '1322', 219.00, 743.20, 'POL_FS2016'),
       ('Redbridge', '1323', 225.39, 764.17, 'POL_FS2016'),
       ('Richmond-Upon-Thames', '1324', 240.90, 815.42, 'POL_FS2016'),
       ('South London', '1325', 229.95, 778.16, 'POL_FS2016'),
       ('Sutton', '1326', 218.09, 738.55, 'POL_FS2016'),
       ('Thames', '1327', 218.09, 738.55, 'POL_FS2016')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Tower Bridge', '1328', 232.69, 789.80, 'POL_FS2016'),
       ('Uxbridge', '1329', 210.79, 715.25, 'POL_FS2016'),
       ('Waltham Forest', '1330', 204.40, 694.28, 'POL_FS2016'),
       ('West London', '1331', 235.43, 799.10, 'POL_FS2016'),
       ('Wimbledon', '1332', 223.56, 757.19, 'POL_FS2016'),
       ('Avon North & Thornbury', '2001', 177.94, 561.49, 'POL_FS2016'),
       ('Bath', '2002', 193.37, 580.10, 'POL_FS2016'),
       ('Mendip & South Somerset', '2003', 216.67, 650.02, 'POL_FS2016'),
       ('Bristol', '2004', 159.98, 479.94, 'POL_FS2016'),
       ('Sedgemore / Taunton Deane', '2005', 181.59, 615.06, 'POL_FS2016')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Weston-Super-Mare', '2006', 180.95, 542.85, 'POL_FS2016'),
       ('Central Dorset', '2007', 182.50, 547.50, 'POL_FS2016'),
       ('Bournemouth & Christchurch', '2008', 145.22, 435.67, 'POL_FS2016'),
       ('Poole East Dorset', '2009', 153.30, 470.60, 'POL_FS2016'),
       ('Bridport / West Dorset', '2010', 146.00, 438.00, 'POL_FS2016'),
       ('Salisbury', '2011', 174.29, 535.84, 'POL_FS2016'),
       ('Chippenham / Trowbridge', '2012', 187.94, 563.82, 'POL_FS2016'),
       ('Swindon', '2013', 171.55, 528.86, 'POL_FS2016'),
       ('Cheltenham', '2014', 157.86, 486.92, 'POL_FS2016'),
       ('Gloucester', '2015', 155.13, 477.61, 'POL_FS2016')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Stroud', '2016', 177.94, 547.50, 'POL_FS2016'),
       ('Barnstaple', '2017', 173.96, 521.88, 'POL_FS2016'),
       ('Exeter', '2018', 154.54, 463.62, 'POL_FS2016'),
       ('Plymouth', '2019', 179.40, 538.19, 'POL_FS2016'),
       ('East Cornwall', '2020', 198.93, 675.64, 'POL_FS2016'),
       ('Carrick / Kerrier (Camborne) / Penwith', '2021', 177.94, 563.82, 'POL_FS2016'),
       ('Teignbridge / Torbay', '2022', 163.17, 489.25, 'POL_FS2016'),
       ('Stoke on Trent / Leek', '3001', 177.94, 563.82, 'POL_FS2016'),
       ('Stafford / Cannock & Rugeley', '3002', 177.94, 547.50, 'POL_FS2016'),
       ('Lichfield & Tamworth / Burton Upon Trent / Uttoxeter', '3003', 172.46, 531.18, 'POL_FS2016')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Leamington / Nuneaton / Rugby', '3004', 178.61, 535.84, 'POL_FS2016'),
       ('Hereford / Leominster', '3005', 155.32, 465.95, 'POL_FS2016'),
       ('Kidderminster / Redditch', '3006', 198.81, 596.42, 'POL_FS2016'),
       ('Shrewsbury', '3007', 166.08, 510.22, 'POL_FS2016'),
       ('Telford', '3008', 172.46, 531.18, 'POL_FS2016'),
       ('Worcester', '3009', 180.95, 542.85, 'POL_FS2016'),
       ('Sandwell', '3010', 176.11, 540.52, 'POL_FS2016'),
       ('Wolverhampton & Seisdon', '3011', 176.11, 540.52, 'POL_FS2016'),
       ('Dudley & Halesowen', '3012', 173.18, 519.55, 'POL_FS2016'),
       ('Walsall', '3013', 177.94, 549.83, 'POL_FS2016')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Birmingham', '3014', 177.94, 566.14, 'POL_FS2016'),
       ('Solihull', '3015', 187.16, 561.49, 'POL_FS2016'),
       ('Coventry', '3016', 153.77, 461.30, 'POL_FS2016'),
       ('Amman Valley', '4001', 177.94, 570.80, 'POL_FS2016'),
       ('Carmarthen East Dyfed', '4002', 201.92, 605.75, 'POL_FS2016'),
       ('Llanelli', '4003', 138.70, 426.34, 'POL_FS2016'),
       ('Brecon & Radnor', '4004', 203.47, 610.41, 'POL_FS2016'),
       ('Mid Wales', '4005', 155.32, 465.95, 'POL_FS2016'),
       ('North Ceredigion / South Ceredigion', '4006', 204.24, 612.73, 'POL_FS2016'),
       ('Pembrokeshire', '4007', 166.99, 514.90, 'POL_FS2016')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('East Gwent', '4008', 169.73, 521.88, 'POL_FS2016'),
       ('Newport', '4009', 166.99, 512.54, 'POL_FS2016'),
       ('Lower Rhymney Valley / North Bedwellty / South Bedwellty', '4010', 177.94, 556.81, 'POL_FS2016'),
       ('Bangor & Caernarfon', '4011', 189.49, 568.47, 'POL_FS2016'),
       ('Colwyn Bay', '4012', 173.38, 533.51, 'POL_FS2016'),
       ('Denbighshire', '4013', 188.71, 566.14, 'POL_FS2016'),
       ('Dolgellau', '4014', 188.71, 566.14, 'POL_FS2016'),
       ('Mold & Hawarden', '4015', 177.94, 554.48, 'POL_FS2016'),
       ('North Anglesey', '4016', 197.26, 591.77, 'POL_FS2016'),
       ('Pwllheli', '4017', 133.57, 400.72, 'POL_FS2016')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Wrexham', '4018', 161.53, 484.59, 'POL_FS2016'),
       ('Cardiff', '4019', 177.94, 587.11, 'POL_FS2016'),
       ('Vale of Glamorgan', '4020', 208.13, 624.40, 'POL_FS2016'),
       ('Cynon Valley', '4021', 177.94, 563.82, 'POL_FS2016'),
       ('Mid Glamorgan & Miskin', '4022', 177.94, 587.11, 'POL_FS2016'),
       ('Merthyr Tydfil', '4023', 177.94, 582.46, 'POL_FS2016'),
       ('Port Talbot', '4024', 219.00, 740.88, 'POL_FS2016'),
       ('Newcastle & Ogmore', '4025', 177.94, 596.42, 'POL_FS2016'),
       ('Neath', '4026', 180.68, 612.73, 'POL_FS2016'),
       ('Swansea', '4027', 171.55, 528.86, 'POL_FS2016')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Bootle & Crosby', '5001', 162.43, 498.58, 'POL_FS2016'),
       ('Southport', '5002', 135.91, 407.72, 'POL_FS2016'),
       ('Liverpool', '5003', 179.40, 538.19, 'POL_FS2016'),
       ('St Helens', '5004', 153.30, 472.96, 'POL_FS2016'),
       ('Knowsley', '5005', 165.16, 507.89, 'POL_FS2016'),
       ('Wirral', '5006', 157.86, 484.59, 'POL_FS2016'),
       ('Crewe & Nantwich / Sandbach & Congleton / Macclesfield', '6001', 176.11, 540.52, 'POL_FS2016'),
       ('Warrington / Halton', '6002', 154.54, 463.62, 'POL_FS2016'),
       ('Chester / Vale Royal (Northwich)', '6003', 160.76, 482.27, 'POL_FS2016'),
       ('Barrow In Furness', '6004', 153.77, 461.30, 'POL_FS2016')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Kendal & Windermere', '6005', 183.28, 549.83, 'POL_FS2016'),
       ('Penrith / Carlisle', '6006', 173.18, 519.55, 'POL_FS2016'),
       ('Whitehaven / Workington', '6007', 143.67, 431.02, 'POL_FS2016'),
       ('Manchester', '6008', 177.94, 587.11, 'POL_FS2016'),
       ('Stockport', '6009', 167.74, 503.23, 'POL_FS2016'),
       ('Trafford', '6010', 177.94, 559.16, 'POL_FS2016'),
       ('Salford', '6011', 177.94, 570.80, 'POL_FS2016'),
       ('Bolton', '6012', 164.64, 493.93, 'POL_FS2016'),
       ('Bury', '6013', 159.98, 479.94, 'POL_FS2016'),
       ('Wigan', '6014', 170.07, 510.22, 'POL_FS2016')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Rochdale / Middleton', '6015', 169.30, 507.89, 'POL_FS2016'),
       ('Tameside', '6016', 156.04, 479.94, 'POL_FS2016'),
       ('Oldham', '6017', 137.46, 412.38, 'POL_FS2016'),
       ('Burnley / Rossendale', '6018', 162.31, 486.92, 'POL_FS2016'),
       ('Blackburn / Accrington / Ribble Valley', '6019', 177.94, 580.10, 'POL_FS2016'),
       ('Blackpool', '6020', 126.58, 379.75, 'POL_FS2016'),
       ('Fleetwood', '6021', 129.69, 389.08, 'POL_FS2016'),
       ('Lancaster', '6022', 159.20, 477.61, 'POL_FS2016'),
       ('Chorley / Ormskirk / South Ribble & Leyland', '6023', 174.73, 524.20, 'POL_FS2016'),
       ('Preston', '6024', 142.90, 428.69, 'POL_FS2016')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Dartford & Gravesend', '7001', 232.98, 698.94, 'POL_FS2016'),
       ('Ashford & Tenterden / Dover / Folkestone', '7002', 205.31, 696.61, 'POL_FS2016'),
       ('Medway', '7003', 205.02, 615.06, 'POL_FS2016'),
       ('Swale', '7004', 243.07, 729.22, 'POL_FS2016'),
       ('Maidstone & West Malling', '7005', 216.67, 650.02, 'POL_FS2016'),
       ('Canterbury / Thanet', '7006', 177.94, 603.43, 'POL_FS2016'),
       ('West Kent (Tonbridge)', '7007', 208.13, 624.40, 'POL_FS2016'),
       ('Guildford & Farnham', '7008', 179.76, 610.41, 'POL_FS2016'),
       ('North West Surrey (Woking)', '7009', 196.19, 666.31, 'POL_FS2016'),
       ('South East Surrey', '7010', 207.14, 703.59, 'POL_FS2016')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Epsom', '7011', 209.88, 712.93, 'POL_FS2016'),
       ('Staines', '7012', 240.90, 815.42, 'POL_FS2016'),
       ('Brighton & Hove & Lewes', '7013', 183.41, 622.04, 'POL_FS2016'),
       ('Chichester & District', '7014', 162.43, 498.58, 'POL_FS2016'),
       ('Crawley / Horsham', '7015', 228.32, 684.95, 'POL_FS2016'),
       ('Hastings', '7016', 142.35, 438.00, 'POL_FS2016'),
       ('Worthing', '7017', 164.25, 505.56, 'POL_FS2016'),
       ('Eastbourne', '7018', 173.18, 519.55, 'POL_FS2016'),
       ('East Derbyshire (Ripley) / Ilkeston', '8001', 206.57, 619.72, 'POL_FS2016'),
       ('Ashbourne / Matlock / High Peak (Buxton)', '8002', 190.27, 570.80, 'POL_FS2016')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Chesterfield', '8003', 177.84, 533.51, 'POL_FS2016'),
       ('Derby / Swadlincote', '8004', 177.94, 570.80, 'POL_FS2016'),
       ('Ashby & Coalville / Loughborough / Melton Mowbray', '8005', 181.72, 545.17, 'POL_FS2016'),
       ('Leicester', '8006', 177.94, 552.15, 'POL_FS2016'),
       ('Hinckley / Market Harborough', '8007', 201.92, 605.75, 'POL_FS2016'),
       ('Boston / Bourne / Stamford', '8008', 173.38, 533.51, 'POL_FS2016'),
       ('Skegness', '8009', 156.09, 468.28, 'POL_FS2016'),
       ('Lincoln / Gainsborough', '8010', 161.53, 484.59, 'POL_FS2016'),
       ('Grantham & Sleaford', '8011', 159.69, 491.57, 'POL_FS2016'),
       ('Mansfield', '8012', 160.60, 493.93, 'POL_FS2016')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Newark', '8013', 180.17, 540.52, 'POL_FS2016'),
       ('Nottingham', '8014', 179.40, 538.19, 'POL_FS2016'),
       ('Worksop & East Retford', '8015', 170.64, 524.20, 'POL_FS2016'),
       ('Corby (Kettering) / Wellingborough', '8016', 157.65, 472.96, 'POL_FS2016'),
       ('Northampton', '8017', 170.85, 512.54, 'POL_FS2016'),
       ('Bedford', '9001', 167.90, 517.22, 'POL_FS2016'),
       ('Luton', '9002', 177.94, 601.07, 'POL_FS2016'),
       ('Cambridge', '9003', 162.43, 500.91, 'POL_FS2016'),
       ('Ely', '9004', 177.94, 575.45, 'POL_FS2016'),
       ('Huntingdon', '9005', 173.18, 519.55, 'POL_FS2016')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('March & Wisbech', '9006', 171.63, 514.90, 'POL_FS2016'),
       ('Peterborough', '9007', 142.90, 428.69, 'POL_FS2016'),
       ('Basildon', '9008', 177.94, 549.83, 'POL_FS2016'),
       ('Brentwood', '9009', 249.11, 845.72, 'POL_FS2016'),
       ('Braintree', '9010', 198.93, 673.32, 'POL_FS2016'),
       ('Clacton & Harwich / Colchester', '9011', 177.94, 563.82, 'POL_FS2016'),
       ('Grays', '9012', 232.69, 789.80, 'POL_FS2016'),
       ('Harlow & Loughton', '9013', 232.69, 789.80, 'POL_FS2016'),
       ('Stansted', '9014', 257.33, 873.67, 'POL_FS2016'),
       ('Rayleigh / Southend On Sea', '9015', 166.97, 500.91, 'POL_FS2016')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Chelmsford / Witham', '9016', 176.11, 542.85, 'POL_FS2016'),
       ('Dacorum (Hemel Hempstead)', '9017', 209.88, 710.57, 'POL_FS2016'),
       ('Bishop''s Stortford / East Hertfordshire', '9018', 254.59, 864.34, 'POL_FS2016'),
       ('Stevenage & North Hertfordshire', '9019', 236.34, 801.46, 'POL_FS2016'),
       ('St Albans', '9020', 214.44, 702.25, 'POL_FS2016'),
       ('Watford', '9021', 210.79, 715.25, 'POL_FS2016'),
       ('Cromer & North Walsham', '9022', 184.33, 624.40, 'POL_FS2016'),
       ('Great Yarmouth', '9023', 168.52, 505.56, 'POL_FS2016'),
       ('Kings Lynn & West Norfolk', '9024', 164.64, 493.93, 'POL_FS2016'),
       ('Norwich & District', '9025', 169.30, 507.89, 'POL_FS2016')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Diss / Thetford', '9026', 175.20, 538.19, 'POL_FS2016'),
       ('Dereham', '9027', 198.01, 670.99, 'POL_FS2016'),
       ('Lowestoft / Beccles & Halesworth / Aldeburgh', '9028', 169.30, 507.89, 'POL_FS2016'),
       ('Felixstowe / Ipswich & District / Woodbridge', '9029', 172.41, 517.22, 'POL_FS2016'),
       ('Sudbury & Hadleigh / Bury St Edmunds / Haverhill / Newmarket', '9030', 177.94, 552.15, 'POL_FS2016'),
       ('Hartlepool', '1001', 151.11, 466.21, 'POL_FS2022'),
       ('Teeside', '1002', 156.35, 479.58, 'POL_FS2022'),
       ('Darlington', '1003', 177.72, 533.16, 'POL_FS2022'),
       ('South Durham', '1004', 175.25, 538.52, 'POL_FS2022'),
       ('Durham', '1005', 204.63, 637.65, 'POL_FS2022')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Derwentside', '1006', 197.37, 592.14, 'POL_FS2022'),
       ('Easington', '1007', 192.04, 589.42, 'POL_FS2022'),
       ('South East Northumberland', '1008', 170.58, 511.73, 'POL_FS2022'),
       ('Newcastle upon Tyne', '1009', 158.46, 487.61, 'POL_FS2022'),
       ('Gateshead', '1010', 164.34, 492.99, 'POL_FS2022'),
       ('North Tyneside', '1011', 161.61, 495.67, 'POL_FS2022'),
       ('South Tyneside', '1012', 153.21, 471.56, 'POL_FS2022'),
       ('Sunderland / Houghton Le Spring', '1013', 171.05, 527.82, 'POL_FS2022'),
       ('Berwick & Alnwick', '1014', 203.58, 626.95, 'POL_FS2022'),
       ('Tynedale & Hexham', '1015', 177.34, 546.57, 'POL_FS2022')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Abingdon, Didcot & Witney (South Oxfordshire)', '1131', 240.30, 814.49, 'POL_FS2022'),
       ('Aylesbury', '1132', 228.63, 685.88, 'POL_FS2022'),
       ('High Wycombe & Amersham', '1133', 219.32, 744.84, 'POL_FS2022'),
       ('Milton Keynes', '1134', 189.93, 584.07, 'POL_FS2022'),
       ('Bicester / North Oxon (Banbury)', '1135', 223.51, 758.23, 'POL_FS2022'),
       ('Oxford', '1136', 223.51, 758.23, 'POL_FS2022'),
       ('Reading', '1137', 217.02, 651.06, 'POL_FS2022'),
       ('Slough (East Berkshire)', '1138', 240.30, 814.49, 'POL_FS2022'),
       ('West Berkshire (Newbury Etc)', '1139', 200.94, 602.83, 'POL_FS2022'),
       ('Aldershot / Petersfield (North East Hampshire)', '1140', 229.82, 779.67, 'POL_FS2022')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Andover / Basingstoke / Winchester (NW Hants)', '1141', 242.03, 726.09, 'POL_FS2022'),
       ('Isle Of Wight', '1142', 197.37, 592.14, 'POL_FS2022'),
       ('Portsmouth / Waterlooville (South East Hampshire)', '1143', 202.73, 608.19, 'POL_FS2022'),
       ('Gosport & Fareham', '1144', 247.38, 742.14, 'POL_FS2022'),
       ('Southampton (South West Hampshire)', '1145', 228.63, 685.88, 'POL_FS2022'),
       ('Grimsby & Cleethorpes', '1201', 154.50, 463.50, 'POL_FS2022'),
       ('Scunthorpe', '1202', 165.81, 511.73, 'POL_FS2022'),
       ('Hull', '1203', 176.30, 541.19, 'POL_FS2022'),
       ('Beverley / Bridlington', '1204', 204.63, 675.18, 'POL_FS2022'),
       ('Goole', '1205', 209.88, 710.00, 'POL_FS2022')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Northallerton & Richmond', '1206', 220.59, 661.77, 'POL_FS2022'),
       ('Harrogate & Ripon', '1207', 211.66, 634.97, 'POL_FS2022'),
       ('Skipton, Settle & Ingleton', '1208', 204.63, 629.63, 'POL_FS2022'),
       ('Scarborough / Whitby', '1209', 175.25, 538.52, 'POL_FS2022'),
       ('Malton & Ryedale', '1210', 168.80, 506.38, 'POL_FS2022'),
       ('York / Selby', '1211', 183.64, 565.31, 'POL_FS2022'),
       ('Barnsley', '1212', 182.60, 562.64, 'POL_FS2022'),
       ('Doncaster', '1213', 176.30, 541.19, 'POL_FS2022'),
       ('Rotherham', '1214', 186.79, 576.05, 'POL_FS2022'),
       ('Sheffield', '1215', 192.04, 592.14, 'POL_FS2022')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Halifax', '1216', 200.05, 600.16, 'POL_FS2022'),
       ('Huddersfield', '1217', 168.80, 506.38, 'POL_FS2022'),
       ('Dewsbury', '1218', 183.08, 549.25, 'POL_FS2022'),
       ('Bradford', '1219', 156.35, 482.26, 'POL_FS2022'),
       ('Keighley & Bingley', '1220', 176.30, 541.19, 'POL_FS2022'),
       ('Leeds', '1221', 165.81, 509.05, 'POL_FS2022'),
       ('Pontefract & Castleford', '1222', 162.54, 487.61, 'POL_FS2022'),
       ('Wakefield', '1223', 160.55, 492.99, 'POL_FS2022'),
       ('Barking', '1301', 258.15, 876.13, 'POL_FS2022'),
       ('Bexley', '1302', 230.86, 782.35, 'POL_FS2022')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Bishopsgate', '1303', 269.69, 913.62, 'POL_FS2022'),
       ('Brent', '1304', 251.85, 852.01, 'POL_FS2022'),
       ('Brentford', '1305', 256.05, 868.07, 'POL_FS2022'),
       ('Bromley', '1306', 243.46, 825.22, 'POL_FS2022'),
       ('Camberwell Green', '1307', 251.85, 854.68, 'POL_FS2022'),
       ('Central London', '1308', 272.84, 924.35, 'POL_FS2022'),
       ('Clerkenwell/Hampstead', '1309', 255.00, 862.71, 'POL_FS2022'),
       ('Croydon', '1310', 248.70, 841.27, 'POL_FS2022'),
       ('Ealing', '1311', 264.44, 897.77, 'POL_FS2022'),
       ('Enfield', '1312', 250.80, 849.33, 'POL_FS2022')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Greenwich/Woolwich', '1313', 240.30, 814.49, 'POL_FS2022'),
       ('Haringey', '1314', 259.20, 878.80, 'POL_FS2022'),
       ('Harrow', '1315', 251.85, 854.68, 'POL_FS2022'),
       ('Havering', '1316', 235.06, 795.75, 'POL_FS2022'),
       ('Heathrow', '1317', 315.86, 1071.72, 'POL_FS2022'),
       ('Hendon/Barnet', '1318', 253.95, 860.04, 'POL_FS2022'),
       ('Highbury Corner', '1319', 264.44, 894.88, 'POL_FS2022'),
       ('Kingston-Upon-Thames', '1320', 262.35, 889.50, 'POL_FS2022'),
       ('Newham', '1321', 252.90, 857.36, 'POL_FS2022'),
       ('Old Street', '1322', 251.85, 854.68, 'POL_FS2022')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Redbridge', '1323', 259.20, 878.80, 'POL_FS2022'),
       ('Richmond-Upon-Thames', '1324', 277.04, 937.73, 'POL_FS2022'),
       ('South London', '1325', 264.44, 894.88, 'POL_FS2022'),
       ('Sutton', '1326', 250.80, 849.33, 'POL_FS2022'),
       ('Thames', '1327', 250.80, 849.33, 'POL_FS2022'),
       ('Tower Bridge', '1328', 267.59, 908.27, 'POL_FS2022'),
       ('Uxbridge', '1329', 242.41, 822.54, 'POL_FS2022'),
       ('Waltham Forest', '1330', 235.06, 798.42, 'POL_FS2022'),
       ('West London', '1331', 270.74, 918.97, 'POL_FS2022'),
       ('Wimbledon', '1332', 257.09, 870.77, 'POL_FS2022')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Bury', '6013', 183.98, 551.93, 'POL_FS2022'),
       ('Avon North & Thornbury', '2001', 204.63, 645.71, 'POL_FS2022'),
       ('Bath', '2002', 222.38, 667.12, 'POL_FS2022'),
       ('Mendip & South Somerset', '2003', 249.17, 747.52, 'POL_FS2022'),
       ('Bristol', '2004', 183.98, 551.93, 'POL_FS2022'),
       ('Sedgemore / Taunton Deane', '2005', 208.83, 707.32, 'POL_FS2022'),
       ('Weston-Super-Mare', '2006', 209.09, 624.28, 'POL_FS2022'),
       ('Central Dorset', '2007', 209.88, 629.63, 'POL_FS2022'),
       ('Bournemouth & Christchurch', '2008', 167.00, 501.02, 'POL_FS2022'),
       ('Poole East Dorset', '2009', 176.30, 541.19, 'POL_FS2022')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Bridport / West Dorset', '2010', 167.90, 503.70, 'POL_FS2022'),
       ('Salisbury', '2011', 200.43, 616.22, 'POL_FS2022'),
       ('Chippenham / Trowbridge', '2012', 216.13, 648.39, 'POL_FS2022'),
       ('Swindon', '2013', 197.28, 608.19, 'POL_FS2022'),
       ('Cheltenham', '2014', 181.54, 559.96, 'POL_FS2022'),
       ('Gloucester', '2015', 178.40, 549.25, 'POL_FS2022'),
       ('Stroud', '2016', 204.63, 629.63, 'POL_FS2022'),
       ('Barnstaple', '2017', 200.05, 600.16, 'POL_FS2022'),
       ('Exeter', '2018', 177.72, 533.16, 'POL_FS2022'),
       ('Plymouth', '2019', 206.31, 618.92, 'POL_FS2022')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('East Cornwall', '2020', 228.77, 776.99, 'POL_FS2022'),
       ('Carrick / Kerrier (Camborne) / Penwith', '2021', 204.63, 648.39, 'POL_FS2022'),
       ('Teignbridge / Torbay', '2022', 187.65, 562.64, 'POL_FS2022'),
       ('Stoke on Trent / Leek', '3001', 204.63, 648.39, 'POL_FS2022'),
       ('Stafford / Cannock & Rugeley', '3002', 204.63, 629.63, 'POL_FS2022'),
       ('Lichfield & Tamworth / Burton Upon Trent / Uttoxeter', '3003', 198.33, 610.86, 'POL_FS2022'),
       ('Leamington / Nuneaton / Rugby', '3004', 205.40, 616.22, 'POL_FS2022'),
       ('Hereford / Leominster', '3005', 178.62, 535.84, 'POL_FS2022'),
       ('Kidderminster / Redditch', '3006', 228.63, 685.88, 'POL_FS2022'),
       ('Shrewsbury', '3007', 190.99, 586.75, 'POL_FS2022')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Telford', '3008', 198.33, 610.86, 'POL_FS2022'),
       ('Worcester', '3009', 208.09, 624.28, 'POL_FS2022'),
       ('Sandwell', '3010', 202.53, 621.60, 'POL_FS2022'),
       ('Wolverhampton & Seisdon', '3011', 202.53, 621.60, 'POL_FS2022'),
       ('Dudley & Halesowen', '3012', 199.16, 597.48, 'POL_FS2022'),
       ('Walsall', '3013', 204.63, 632.30, 'POL_FS2022'),
       ('Birmingham', '3014', 204.63, 651.06, 'POL_FS2022'),
       ('Solihull', '3015', 215.23, 645.71, 'POL_FS2022'),
       ('Coventry', '3016', 176.84, 530.50, 'POL_FS2022'),
       ('Amman Valley', '4001', 204.63, 656.42, 'POL_FS2022')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Carmarthen East Dyfed', '4002', 232.21, 696.61, 'POL_FS2022'),
       ('Llanelli', '4003', 159.51, 490.29, 'POL_FS2022'),
       ('Brecon & Radnor', '4004', 233.99, 701.97, 'POL_FS2022'),
       ('Mid Wales', '4005', 178.62, 535.84, 'POL_FS2022'),
       ('North Ceredigion / South Ceredigion', '4006', 234.88, 704.64, 'POL_FS2022'),
       ('Pembrokeshire', '4007', 192.04, 592.14, 'POL_FS2022'),
       ('East Gwent', '4008', 195.19, 600.16, 'POL_FS2022'),
       ('Newport', '4009', 192.04, 589.42, 'POL_FS2022'),
       ('Lower Rhymney Valley / North Bedwellty / South Bedwellty', '4010', 204.63, 640.33, 'POL_FS2022'),
       ('Bangor & Caernarfon', '4011', 217.91, 653.74, 'POL_FS2022')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Colwyn Bay', '4012', 199.39, 613.54, 'POL_FS2022'),
       ('Denbighshire', '4013', 217.02, 651.06, 'POL_FS2022'),
       ('Dolgellau', '4014', 217.02, 651.06, 'POL_FS2022'),
       ('Mold & Hawarden', '4015', 204.63, 637.65, 'POL_FS2022'),
       ('North Anglesey', '4016', 226.85, 680.54, 'POL_FS2022'),
       ('Pwllheli', '4017', 153.61, 460.83, 'POL_FS2022'),
       ('Wrexham', '4018', 185.76, 557.28, 'POL_FS2022'),
       ('Cardiff', '4019', 204.63, 675.18, 'POL_FS2022'),
       ('Vale of Glamorgan', '4020', 239.35, 718.06, 'POL_FS2022'),
       ('Cynon Valley', '4021', 204.63, 648.39, 'POL_FS2022')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Mid Glamorgan & Miskin', '4022', 204.63, 675.18, 'POL_FS2022'),
       ('Merthyr Tydfil', '4023', 204.63, 669.83, 'POL_FS2022'),
       ('Port Talbot', '4024', 251.85, 852.01, 'POL_FS2022'),
       ('Newcastle & Ogmore', '4025', 204.63, 685.88, 'POL_FS2022'),
       ('Neath', '4026', 207.78, 704.64, 'POL_FS2022'),
       ('Swansea', '4027', 197.28, 608.19, 'POL_FS2022'),
       ('Bootle & Crosby', '5001', 186.79, 573.37, 'POL_FS2022'),
       ('Southport', '5002', 156.30, 468.88, 'POL_FS2022'),
       ('Liverpool', '5003', 206.31, 618.92, 'POL_FS2022'),
       ('St Helens', '5004', 176.30, 543.90, 'POL_FS2022')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Knowsley', '5005', 189.93, 584.07, 'POL_FS2022'),
       ('Wirral', '5006', 181.54, 557.28, 'POL_FS2022'),
       ('Crewe & Nantwich / Sandbach & Congleton / Macclesfield', '6001', 202.53, 621.60, 'POL_FS2022'),
       ('Warrington / Halton', '6002', 177.72, 533.16, 'POL_FS2022'),
       ('Chester / Vale Royal (Northwich)', '6003', 184.87, 554.61, 'POL_FS2022'),
       ('Barrow In Furness', '6004', 176.84, 530.50, 'POL_FS2022'),
       ('Kendal & Windermere', '6005', 210.77, 632.30, 'POL_FS2022'),
       ('Penrith / Carlisle', '6006', 199.16, 597.48, 'POL_FS2022'),
       ('Whitehaven / Workington', '6007', 165.22, 495.67, 'POL_FS2022'),
       ('Manchester', '6008', 204.63, 675.18, 'POL_FS2022')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Stockport', '6009', 192.90, 578.71, 'POL_FS2022'),
       ('Trafford', '6010', 204.63, 643.03, 'POL_FS2022'),
       ('Salford', '6011', 204.63, 656.42, 'POL_FS2022'),
       ('Bolton', '6012', 189.34, 568.02, 'POL_FS2022'),
       ('Wigan', '6014', 195.58, 586.75, 'POL_FS2022'),
       ('Rochdale / Middleton', '6015', 194.70, 584.07, 'POL_FS2022'),
       ('Tameside', '6016', 179.45, 551.93, 'POL_FS2022'),
       ('Oldham', '6017', 158.08, 474.24, 'POL_FS2022'),
       ('Burnley / Rossendale', '6018', 186.66, 559.96, 'POL_FS2022'),
       ('Blackburn / Accrington / Ribble Valley', '6019', 204.63, 667.12, 'POL_FS2022')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Blackpool', '6020', 145.57, 436.71, 'POL_FS2022'),
       ('Fleetwood', '6021', 149.14, 447.44, 'POL_FS2022'),
       ('Lancaster', '6022', 183.08, 549.25, 'POL_FS2022'),
       ('Chorley / Ormskirk / South Ribble & Leyland', '6023', 200.94, 602.83, 'POL_FS2022'),
       ('Preston', '6024', 164.34, 492.99, 'POL_FS2022'),
       ('Dartford & Gravesend', '7001', 267.93, 803.78, 'POL_FS2022'),
       ('Ashford & Tenterden / Dover / Folkestone', '7002', 236.11, 801.10, 'POL_FS2022'),
       ('Medway', '7003', 235.77, 707.32, 'POL_FS2022'),
       ('Swale', '7004', 279.53, 838.60, 'POL_FS2022'),
       ('Maidstone & West Malling', '7005', 249.17, 747.52, 'POL_FS2022')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Canterbury / Thanet', '7006', 204.63, 693.94, 'POL_FS2022'),
       ('West Kent (Tonbridge)', '7007', 239.35, 718.06, 'POL_FS2022'),
       ('Guildford & Farnham', '7008', 206.72, 701.97, 'POL_FS2022'),
       ('North West Surrey (Woking)', '7009', 225.62, 766.26, 'POL_FS2022'),
       ('South East Surrey', '7010', 238.21, 809.13, 'POL_FS2022'),
       ('Epsom', '7011', 241.36, 819.87, 'POL_FS2022'),
       ('Staines', '7012', 277.04, 937.73, 'POL_FS2022'),
       ('Brighton & Hove & Lewes', '7013', 210.92, 715.35, 'POL_FS2022'),
       ('Chichester & District', '7014', 186.79, 573.37, 'POL_FS2022'),
       ('Crawley / Horsham', '7015', 262.57, 787.69, 'POL_FS2022')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Hastings', '7016', 163.70, 503.70, 'POL_FS2022'),
       ('Worthing', '7017', 188.89, 581.39, 'POL_FS2022'),
       ('Eastbourne', '7018', 199.16, 597.48, 'POL_FS2022'),
       ('East Derbyshire (Ripley) / Ilkeston', '8001', 237.56, 712.68, 'POL_FS2022'),
       ('Ashbourne / Matlock / High Peak (Buxton)', '8002', 218.81, 656.42, 'POL_FS2022'),
       ('Chesterfield', '8003', 204.52, 613.54, 'POL_FS2022'),
       ('Derby / Swadlincote', '8004', 204.63, 656.42, 'POL_FS2022'),
       ('Ashby & Coalville / Loughborough / Melton Mowbray', '8005', 208.98, 626.95, 'POL_FS2022'),
       ('Leicester', '8006', 204.63, 634.97, 'POL_FS2022'),
       ('Hinckley / Market Harborough', '8007', 232.21, 696.61, 'POL_FS2022')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Boston / Bourne / Stamford', '8008', 199.39, 613.54, 'POL_FS2022'),
       ('Skegness', '8009', 179.50, 538.52, 'POL_FS2022'),
       ('Lincoln / Gainsborough', '8010', 185.76, 557.28, 'POL_FS2022'),
       ('Grantham & Sleaford', '8011', 183.64, 565.31, 'POL_FS2022'),
       ('Mansfield', '8012', 184.69, 568.02, 'POL_FS2022'),
       ('Newark', '8013', 207.20, 621.60, 'POL_FS2022'),
       ('Nottingham', '8014', 206.31, 618.92, 'POL_FS2022'),
       ('Worksop & East Retford', '8015', 196.24, 602.83, 'POL_FS2022'),
       ('Corby (Kettering) / Wellingborough', '8016', 181.30, 543.90, 'POL_FS2022'),
       ('Northampton', '8017', 196.48, 589.42, 'POL_FS2022')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Bedford', '9001', 193.09, 594.80, 'POL_FS2022'),
       ('Luton', '9002', 204.63, 691.23, 'POL_FS2022'),
       ('Cambridge', '9003', 186.79, 576.05, 'POL_FS2022'),
       ('Ely', '9004', 204.63, 661.77, 'POL_FS2022'),
       ('Huntingdon', '9005', 199.16, 597.48, 'POL_FS2022'),
       ('March & Wisbech', '9006', 197.37, 592.14, 'POL_FS2022'),
       ('Peterborough', '9007', 164.34, 492.99, 'POL_FS2022'),
       ('Basildon', '9008', 204.63, 632.30, 'POL_FS2022'),
       ('Brentwood', '9009', 286.48, 972.58, 'POL_FS2022'),
       ('Braintree', '9010', 228.77, 774.32, 'POL_FS2022')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Clacton & Harwich / Colchester', '9011', 204.63, 648.39, 'POL_FS2022'),
       ('Grays', '9012', 267.59, 908.27, 'POL_FS2022'),
       ('Harlow & Loughton', '9013', 267.59, 908.27, 'POL_FS2022'),
       ('Stansted', '9014', 295.93, 1004.72, 'POL_FS2022'),
       ('Rayleigh / Southend On Sea', '9015', 192.02, 576.05, 'POL_FS2022'),
       ('Chelmsford / Witham', '9016', 202.53, 624.28, 'POL_FS2022'),
       ('Dacorum (Hemel Hempstead)', '9017', 241.36, 817.16, 'POL_FS2022'),
       ('Bishop''s Stortford / East Hertfordshire', '9018', 292.78, 993.99, 'POL_FS2022'),
       ('Stevenage & North Hertfordshire', '9019', 271.79, 921.68, 'POL_FS2022'),
       ('St Albans', '9020', 246.61, 807.59, 'POL_FS2022')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Watford', '9021', 242.41, 822.54, 'POL_FS2022'),
       ('Cromer & North Walsham', '9022', 211.98, 718.06, 'POL_FS2022'),
       ('Great Yarmouth', '9023', 193.80, 581.39, 'POL_FS2022'),
       ('Kings Lynn & West Norfolk', '9024', 189.34, 568.02, 'POL_FS2022'),
       ('Norwich & District', '9025', 194.70, 584.07, 'POL_FS2022'),
       ('Diss / Thetford', '9026', 201.48, 618.92, 'POL_FS2022'),
       ('Dereham', '9027', 227.71, 771.64, 'POL_FS2022'),
       ('Lowestoft / Beccles & Halesworth / Aldeburgh', '9028', 194.70, 584.07, 'POL_FS2022'),
       ('Felixstowe / Ipswich & District / Woodbridge', '9029', 198.27, 594.80, 'POL_FS2022'),
       ('Sudbury & Hadleigh / Bury St Edmunds / Haverhill / Newmarket', '9030', 204.63, 634.97, 'POL_FS2022')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Hartlepool', '1001', 223.52, 588.99, 'POL_FS2024'),
       ('Teeside', '1002', 223.52, 588.99, 'POL_FS2024'),
       ('Darlington', '1003', 223.52, 588.99, 'POL_FS2024'),
       ('South Durham', '1004', 223.52, 588.99, 'POL_FS2024'),
       ('Durham', '1005', 223.52, 588.99, 'POL_FS2024'),
       ('Derwentside', '1006', 223.52, 588.99, 'POL_FS2024'),
       ('Easington', '1007', 223.52, 588.99, 'POL_FS2024'),
       ('South East Northumberland', '1008', 223.52, 588.99, 'POL_FS2024'),
       ('Newcastle upon Tyne', '1009', 223.52, 588.99, 'POL_FS2024'),
       ('Gateshead', '1010', 223.52, 588.99, 'POL_FS2024')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('North Tyneside', '1011', 223.52, 588.99, 'POL_FS2024'),
       ('South Tyneside', '1012', 223.52, 588.99, 'POL_FS2024'),
       ('Sunderland / Houghton Le Spring', '1013', 223.52, 588.99, 'POL_FS2024'),
       ('Berwick & Alnwick', '1014', 223.52, 588.99, 'POL_FS2024'),
       ('Tynedale & Hexham', '1015', 223.52, 588.99, 'POL_FS2024'),
       ('Abingdon, Didcot & Witney (South Oxfordshire)', '1131', 240.30, 814.49, 'POL_FS2024'),
       ('Aylesbury', '1132', 228.63, 685.88, 'POL_FS2024'),
       ('High Wycombe & Amersham', '1133', 223.52, 588.99, 'POL_FS2024'),
       ('Milton Keynes', '1134', 223.52, 588.99, 'POL_FS2024'),
       ('Bicester / North Oxon (Banbury)', '1135', 223.52, 588.99, 'POL_FS2024')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Oxford', '1136', 223.52, 588.99, 'POL_FS2024'),
       ('Reading', '1137', 223.52, 588.99, 'POL_FS2024'),
       ('Slough (East Berkshire)', '1138', 240.30, 814.49, 'POL_FS2024'),
       ('West Berkshire (Newbury Etc)', '1139', 223.52, 588.99, 'POL_FS2024'),
       ('Aldershot / Petersfield (North East Hampshire)', '1140', 229.82, 779.67, 'POL_FS2024'),
       ('Andover / Basingstoke / Winchester (NW Hants)', '1141', 242.03, 726.09, 'POL_FS2024'),
       ('Isle Of Wight', '1142', 223.52, 588.99, 'POL_FS2024'),
       ('Portsmouth / Waterlooville (South East Hampshire)', '1143', 223.52, 588.99, 'POL_FS2024'),
       ('Gosport & Fareham', '1144', 247.38, 742.14, 'POL_FS2024'),
       ('Southampton (South West Hampshire)', '1145', 228.63, 685.88, 'POL_FS2024')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Grimsby & Cleethorpes', '1201', 223.52, 588.99, 'POL_FS2024'),
       ('Scunthorpe', '1202', 223.52, 588.99, 'POL_FS2024'),
       ('Hull', '1203', 223.52, 588.99, 'POL_FS2024'),
       ('Beverley / Bridlington', '1204', 223.52, 588.99, 'POL_FS2024'),
       ('Goole', '1205', 223.52, 588.99, 'POL_FS2024'),
       ('Northallerton & Richmond', '1206', 223.52, 588.99, 'POL_FS2024'),
       ('Harrogate & Ripon', '1207', 223.52, 588.99, 'POL_FS2024'),
       ('Skipton, Settle & Ingleton', '1208', 223.52, 588.99, 'POL_FS2024'),
       ('Scarborough / Whitby', '1209', 223.52, 588.99, 'POL_FS2024'),
       ('Malton & Ryedale', '1210', 223.52, 588.99, 'POL_FS2024')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('York / Selby', '1211', 223.52, 588.99, 'POL_FS2024'),
       ('Barnsley', '1212', 223.52, 588.99, 'POL_FS2024'),
       ('Doncaster', '1213', 223.52, 588.99, 'POL_FS2024'),
       ('Rotherham', '1214', 223.52, 588.99, 'POL_FS2024'),
       ('Sheffield', '1215', 223.52, 588.99, 'POL_FS2024'),
       ('Halifax', '1216', 223.52, 588.99, 'POL_FS2024'),
       ('Huddersfield', '1217', 223.52, 588.99, 'POL_FS2024'),
       ('Dewsbury', '1218', 223.52, 588.99, 'POL_FS2024'),
       ('Bradford', '1219', 223.52, 588.99, 'POL_FS2024'),
       ('Keighley & Bingley', '1220', 223.52, 588.99, 'POL_FS2024')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Leeds', '1221', 223.52, 588.99, 'POL_FS2024'),
       ('Pontefract & Castleford', '1222', 223.52, 588.99, 'POL_FS2024'),
       ('Wakefield', '1223', 223.52, 588.99, 'POL_FS2024'),
       ('Barking', '1301', 264.45, 852.79, 'POL_FS2024'),
       ('Bexley', '1302', 264.45, 852.79, 'POL_FS2024'),
       ('Bishopsgate', '1303', 269.69, 913.62, 'POL_FS2024'),
       ('Brent', '1304', 264.45, 852.79, 'POL_FS2024'),
       ('Brentford', '1305', 264.45, 852.79, 'POL_FS2024'),
       ('Bromley', '1306', 264.45, 852.79, 'POL_FS2024'),
       ('Camberwell Green', '1307', 264.45, 852.79, 'POL_FS2024')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Central London', '1308', 272.84, 924.35, 'POL_FS2024'),
       ('Clerkenwell/Hampstead', '1309', 264.45, 852.79, 'POL_FS2024'),
       ('Croydon', '1310', 264.45, 852.79, 'POL_FS2024'),
       ('Ealing', '1311', 264.45, 852.79, 'POL_FS2024'),
       ('Enfield', '1312', 264.45, 852.79, 'POL_FS2024'),
       ('Greenwich/Woolwich', '1313', 264.45, 852.79, 'POL_FS2024'),
       ('Haringey', '1314', 264.45, 852.79, 'POL_FS2024'),
       ('Harrow', '1315', 264.45, 852.79, 'POL_FS2024'),
       ('Havering', '1316', 264.45, 852.79, 'POL_FS2024'),
       ('Heathrow', '1317', 315.86, 1071.72, 'POL_FS2024')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Hendon/Barnet', '1318', 264.45, 852.79, 'POL_FS2024'),
       ('Highbury Corner', '1319', 264.45, 852.79, 'POL_FS2024'),
       ('Kingston-Upon-Thames', '1320', 264.45, 852.79, 'POL_FS2024'),
       ('Newham', '1321', 264.45, 852.79, 'POL_FS2024'),
       ('Old Street', '1322', 264.45, 852.79, 'POL_FS2024'),
       ('Redbridge', '1323', 264.45, 852.79, 'POL_FS2024'),
       ('Richmond-Upon-Thames', '1324', 277.04, 937.73, 'POL_FS2024'),
       ('South London', '1325', 264.45, 852.79, 'POL_FS2024'),
       ('Sutton', '1326', 264.45, 852.79, 'POL_FS2024'),
       ('Thames', '1327', 264.45, 852.79, 'POL_FS2024')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Tower Bridge', '1328', 267.59, 908.27, 'POL_FS2024'),
       ('Uxbridge', '1329', 264.45, 852.79, 'POL_FS2024'),
       ('Waltham Forest', '1330', 264.45, 852.79, 'POL_FS2024'),
       ('West London', '1331', 270.74, 918.97, 'POL_FS2024'),
       ('Wimbledon', '1332', 264.45, 852.79, 'POL_FS2024'),
       ('Avon North & Thornbury', '2001', 223.52, 588.99, 'POL_FS2024'),
       ('Bath', '2002', 223.52, 588.99, 'POL_FS2024'),
       ('Mendip & South Somerset', '2003', 249.17, 747.52, 'POL_FS2024'),
       ('Bristol', '2004', 223.52, 588.99, 'POL_FS2024')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Sedgemore / Taunton Deane', '2005', 223.52, 588.99, 'POL_FS2024'),
       ('Weston-Super-Mare', '2006', 223.52, 588.99, 'POL_FS2024'),
       ('Central Dorset', '2007', 223.52, 588.99, 'POL_FS2024'),
       ('Bournemouth & Christchurch', '2008', 223.52, 588.99, 'POL_FS2024'),
       ('Poole East Dorset', '2009', 223.52, 588.99, 'POL_FS2024'),
       ('Bridport / West Dorset', '2010', 223.52, 588.99, 'POL_FS2024'),
       ('Salisbury', '2011', 223.52, 588.99, 'POL_FS2024'),
       ('Chippenham / Trowbridge', '2012', 223.52, 588.99, 'POL_FS2024'),
       ('Swindon', '2013', 223.52, 588.99, 'POL_FS2024'),
       ('Cheltenham', '2014', 223.52, 588.99, 'POL_FS2024')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Gloucester', '2015', 223.52, 588.99, 'POL_FS2024'),
       ('Stroud', '2016', 223.52, 588.99, 'POL_FS2024'),
       ('Barnstaple', '2017', 223.52, 588.99, 'POL_FS2024'),
       ('Exeter', '2018', 223.52, 588.99, 'POL_FS2024'),
       ('Plymouth', '2019', 223.52, 588.99, 'POL_FS2024'),
       ('East Cornwall', '2020', 228.77, 776.99, 'POL_FS2024'),
       ('Carrick / Kerrier (Camborne) / Penwith', '2021', 223.52, 588.99, 'POL_FS2024'),
       ('Teignbridge / Torbay', '2022', 223.52, 588.99, 'POL_FS2024'),
       ('Stoke on Trent / Leek', '3001', 223.52, 588.99, 'POL_FS2024'),
       ('Stafford / Cannock & Rugeley', '3002', 223.52, 588.99, 'POL_FS2024')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Lichfield & Tamworth / Burton Upon Trent / Uttoxeter', '3003', 223.52, 588.99, 'POL_FS2024'),
       ('Leamington / Nuneaton / Rugby', '3004', 223.52, 588.99, 'POL_FS2024'),
       ('Hereford / Leominster', '3005', 223.52, 588.99, 'POL_FS2024'),
       ('Kidderminster / Redditch', '3006', 228.63, 685.88, 'POL_FS2024'),
       ('Shrewsbury', '3007', 223.52, 588.99, 'POL_FS2024'),
       ('Telford', '3008', 223.52, 588.99, 'POL_FS2024'),
       ('Worcester', '3009', 223.52, 588.99, 'POL_FS2024'),
       ('Sandwell', '3010', 223.52, 588.99, 'POL_FS2024'),
       ('Wolverhampton & Seisdon', '3011', 223.52, 588.99, 'POL_FS2024'),
       ('Dudley & Halesowen', '3012', 223.52, 588.99, 'POL_FS2024')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Walsall', '3013', 223.52, 588.99, 'POL_FS2024'),
       ('Birmingham', '3014', 223.52, 588.99, 'POL_FS2024'),
       ('Solihull', '3015', 223.52, 588.99, 'POL_FS2024'),
       ('Coventry', '3016', 223.52, 588.99, 'POL_FS2024'),
       ('Amman Valley', '4001', 223.52, 588.99, 'POL_FS2024'),
       ('Carmarthen East Dyfed', '4002', 232.21, 696.61, 'POL_FS2024'),
       ('Llanelli', '4003', 223.52, 588.99, 'POL_FS2024'),
       ('Brecon & Radnor', '4004', 233.99, 701.97, 'POL_FS2024'),
       ('Mid Wales', '4005', 223.52, 588.99, 'POL_FS2024'),
       ('North Ceredigion / South Ceredigion', '4006', 234.88, 704.64, 'POL_FS2024')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Pembrokeshire', '4007', 223.52, 588.99, 'POL_FS2024'),
       ('East Gwent', '4008', 223.52, 588.99, 'POL_FS2024'),
       ('Newport', '4009', 223.52, 588.99, 'POL_FS2024'),
       ('Lower Rhymney Valley / North Bedwellty / South Bedwellty', '4010', 223.52, 588.99, 'POL_FS2024'),
       ('Bangor & Caernarfon', '4011', 223.52, 588.99, 'POL_FS2024'),
       ('Colwyn Bay', '4012', 223.52, 588.99, 'POL_FS2024'),
       ('Denbighshire', '4013', 223.52, 588.99, 'POL_FS2024'),
       ('Dolgellau', '4014', 223.52, 588.99, 'POL_FS2024'),
       ('Mold & Hawarden', '4015', 223.52, 588.99, 'POL_FS2024'),
       ('North Anglesey', '4016', 226.85, 680.54, 'POL_FS2024')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Pwllheli', '4017', 223.52, 588.99, 'POL_FS2024'),
       ('Wrexham', '4018', 223.52, 588.99, 'POL_FS2024'),
       ('Cardiff', '4019', 223.52, 588.99, 'POL_FS2024'),
       ('Vale of Glamorgan', '4020', 239.35, 718.06, 'POL_FS2024'),
       ('Cynon Valley', '4021', 223.52, 588.99, 'POL_FS2024'),
       ('Mid Glamorgan & Miskin', '4022', 223.52, 588.99, 'POL_FS2024'),
       ('Merthyr Tydfil', '4023', 223.52, 588.99, 'POL_FS2024'),
       ('Port Talbot', '4024', 251.85, 852.01, 'POL_FS2024'),
       ('Newcastle & Ogmore', '4025', 223.52, 588.99, 'POL_FS2024'),
       ('Neath', '4026', 223.52, 588.99, 'POL_FS2024')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Swansea', '4027', 223.52, 588.99, 'POL_FS2024'),
       ('Bootle & Crosby', '5001', 223.52, 588.99, 'POL_FS2024'),
       ('Southport', '5002', 223.52, 588.99, 'POL_FS2024'),
       ('Liverpool', '5003', 223.52, 588.99, 'POL_FS2024'),
       ('St Helens', '5004', 223.52, 588.99, 'POL_FS2024'),
       ('Knowsley', '5005', 223.52, 588.99, 'POL_FS2024'),
       ('Wirral', '5006', 223.52, 588.99, 'POL_FS2024'),
       ('Crewe & Nantwich / Sandbach & Congleton / Macclesfield', '6001', 223.52, 588.99, 'POL_FS2024'),
       ('Warrington / Halton', '6002', 223.52, 588.99, 'POL_FS2024'),
       ('Chester / Vale Royal (Northwich)', '6003', 223.52, 588.99, 'POL_FS2024')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Barrow In Furness', '6004', 223.52, 588.99, 'POL_FS2024'),
       ('Kendal & Windermere', '6005', 223.52, 588.99, 'POL_FS2024'),
       ('Penrith / Carlisle', '6006', 223.52, 588.99, 'POL_FS2024'),
       ('Whitehaven / Workington', '6007', 223.52, 588.99, 'POL_FS2024'),
       ('Manchester', '6008', 223.52, 588.99, 'POL_FS2024'),
       ('Stockport', '6009', 223.52, 588.99, 'POL_FS2024'),
       ('Trafford', '6010', 223.52, 588.99, 'POL_FS2024'),
       ('Salford', '6011', 223.52, 588.99, 'POL_FS2024'),
       ('Bolton', '6012', 223.52, 588.99, 'POL_FS2024'),
       ('Bury', '6013', 223.52, 588.99, 'POL_FS2024')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Wigan', '6014', 223.52, 588.99, 'POL_FS2024'),
       ('Rochdale / Middleton', '6015', 223.52, 588.99, 'POL_FS2024'),
       ('Tameside', '6016', 223.52, 588.99, 'POL_FS2024'),
       ('Oldham', '6017', 223.52, 588.99, 'POL_FS2024'),
       ('Burnley / Rossendale', '6018', 223.52, 588.99, 'POL_FS2024'),
       ('Blackburn / Accrington / Ribble Valley', '6019', 223.52, 588.99, 'POL_FS2024'),
       ('Blackpool', '6020', 223.52, 588.99, 'POL_FS2024'),
       ('Fleetwood', '6021', 223.52, 588.99, 'POL_FS2024'),
       ('Lancaster', '6022', 223.52, 588.99, 'POL_FS2024')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Chorley / Ormskirk / South Ribble & Leyland', '6023', 223.52, 588.99, 'POL_FS2024'),
       ('Preston', '6024', 223.52, 588.99, 'POL_FS2024'),
       ('Dartford & Gravesend', '7001', 267.93, 803.78, 'POL_FS2024'),
       ('Ashford & Tenterden / Dover / Folkestone', '7002', 236.11, 801.10, 'POL_FS2024'),
       ('Medway', '7003', 235.77, 707.32, 'POL_FS2024'),
       ('Swale', '7004', 279.53, 838.60, 'POL_FS2024'),
       ('Maidstone & West Malling', '7005', 249.17, 747.52, 'POL_FS2024'),
       ('Canterbury / Thanet', '7006', 223.52, 588.99, 'POL_FS2024'),
       ('West Kent (Tonbridge)', '7007', 239.35, 718.06, 'POL_FS2024'),
       ('Guildford & Farnham', '7008', 223.52, 588.99, 'POL_FS2024')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('North West Surrey (Woking)', '7009', 225.62, 766.26, 'POL_FS2024'),
       ('South East Surrey', '7010', 238.21, 809.13, 'POL_FS2024'),
       ('Epsom', '7011', 241.36, 819.87, 'POL_FS2024'),
       ('Staines', '7012', 277.04, 937.73, 'POL_FS2024'),
       ('Brighton & Hove & Lewes', '7013', 223.52, 588.99, 'POL_FS2024'),
       ('Chichester & District', '7014', 223.52, 588.99, 'POL_FS2024'),
       ('Crawley / Horsham', '7015', 262.57, 787.69, 'POL_FS2024'),
       ('Hastings', '7016', 223.52, 588.99, 'POL_FS2024'),
       ('Worthing', '7017', 223.52, 588.99, 'POL_FS2024'),
       ('Eastbourne', '7018', 223.52, 588.99, 'POL_FS2024')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('East Derbyshire (Ripley) / Ilkeston', '8001', 237.56, 712.68, 'POL_FS2024'),
       ('Ashbourne / Matlock / High Peak (Buxton)', '8002', 223.52, 588.99, 'POL_FS2024'),
       ('Chesterfield', '8003', 223.52, 588.99, 'POL_FS2024'),
       ('Derby / Swadlincote', '8004', 223.52, 588.99, 'POL_FS2024'),
       ('Ashby & Coalville / Loughborough / Melton Mowbray', '8005', 223.52, 588.99, 'POL_FS2024'),
       ('Leicester', '8006', 223.52, 588.99, 'POL_FS2024'),
       ('Hinckley / Market Harborough', '8007', 232.21, 696.61, 'POL_FS2024'),
       ('Boston / Bourne / Stamford', '8008', 223.52, 588.99, 'POL_FS2024'),
       ('Skegness', '8009', 223.52, 588.99, 'POL_FS2024'),
       ('Lincoln / Gainsborough', '8010', 223.52, 588.99, 'POL_FS2024')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Grantham & Sleaford', '8011', 223.52, 588.99, 'POL_FS2024'),
       ('Mansfield', '8012', 223.52, 588.99, 'POL_FS2024'),
       ('Newark', '8013', 223.52, 588.99, 'POL_FS2024'),
       ('Nottingham', '8014', 223.52, 588.99, 'POL_FS2024'),
       ('Worksop & East Retford', '8015', 223.52, 588.99, 'POL_FS2024'),
       ('Corby (Kettering) / Wellingborough', '8016', 223.52, 588.99, 'POL_FS2024'),
       ('Northampton', '8017', 223.52, 588.99, 'POL_FS2024'),
       ('Bedford', '9001', 223.52, 588.99, 'POL_FS2024'),
       ('Luton', '9002', 223.52, 588.99, 'POL_FS2024'),
       ('Cambridge', '9003', 223.52, 588.99, 'POL_FS2024')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Ely', '9004', 223.52, 588.99, 'POL_FS2024'),
       ('Huntingdon', '9005', 223.52, 588.99, 'POL_FS2024'),
       ('March & Wisbech', '9006', 223.52, 588.99, 'POL_FS2024'),
       ('Peterborough', '9007', 223.52, 588.99, 'POL_FS2024'),
       ('Basildon', '9008', 223.52, 588.99, 'POL_FS2024'),
       ('Brentwood', '9009', 286.48, 972.58, 'POL_FS2024'),
       ('Braintree', '9010', 228.77, 774.32, 'POL_FS2024'),
       ('Clacton & Harwich / Colchester', '9011', 223.52, 588.99, 'POL_FS2024'),
       ('Grays', '9012', 267.59, 908.27, 'POL_FS2024'),
       ('Harlow & Loughton', '9013', 267.59, 908.27, 'POL_FS2024')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Stansted', '9014', 295.93, 1004.72, 'POL_FS2024'),
       ('Rayleigh / Southend On Sea', '9015', 223.52, 588.99, 'POL_FS2024'),
       ('Chelmsford / Witham', '9016', 223.52, 588.99, 'POL_FS2024'),
       ('Dacorum (Hemel Hempstead)', '9017', 241.36, 817.16, 'POL_FS2024'),
       ('Bishop''s Stortford / East Hertfordshire', '9018', 292.78, 993.99, 'POL_FS2024'),
       ('Stevenage & North Hertfordshire', '9019', 271.79, 921.68, 'POL_FS2024'),
       ('St Albans', '9020', 246.61, 807.59, 'POL_FS2024'),
       ('Watford', '9021', 242.41, 822.54, 'POL_FS2024'),
       ('Cromer & North Walsham', '9022', 223.52, 588.99, 'POL_FS2024'),
       ('Great Yarmouth', '9023', 223.52, 588.99, 'POL_FS2024')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Kings Lynn & West Norfolk', '9024', 223.52, 588.99, 'POL_FS2024'),
       ('Norwich & District', '9025', 223.52, 588.99, 'POL_FS2024'),
       ('Diss / Thetford', '9026', 223.52, 588.99, 'POL_FS2024'),
       ('Dereham', '9027', 227.71, 771.64, 'POL_FS2024'),
       ('Lowestoft / Beccles & Halesworth / Aldeburgh', '9028', 223.52, 588.99, 'POL_FS2024'),
       ('Felixstowe / Ipswich & District / Woodbridge', '9029', 223.52, 588.99, 'POL_FS2024'),
       ('Sudbury & Hadleigh / Bury St Edmunds / Haverhill / Newmarket', '9030', 223.52, 588.99, 'POL_FS2024'),
       ('Hartlepool', '1001', 320.00, 650.00, 'POL_FS2025'),
       ('Teeside', '1002', 320.00, 650.00, 'POL_FS2025'),
       ('Darlington', '1003', 320.00, 650.00, 'POL_FS2025')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('South Durham', '1004', 320.00, 650.00, 'POL_FS2025'),
       ('Durham', '1005', 320.00, 650.00, 'POL_FS2025'),
       ('Derwentside', '1006', 320.00, 650.00, 'POL_FS2025'),
       ('Easington', '1007', 320.00, 650.00, 'POL_FS2025'),
       ('South East Northumberland', '1008', 320.00, 650.00, 'POL_FS2025'),
       ('Newcastle upon Tyne', '1009', 320.00, 650.00, 'POL_FS2025'),
       ('Gateshead', '1010', 320.00, 650.00, 'POL_FS2025'),
       ('North Tyneside', '1011', 320.00, 650.00, 'POL_FS2025'),
       ('South Tyneside', '1012', 320.00, 650.00, 'POL_FS2025'),
       ('Sunderland / Houghton Le Spring', '1013', 320.00, 650.00, 'POL_FS2025')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Berwick & Alnwick', '1014', 320.00, 650.00, 'POL_FS2025'),
       ('Tynedale & Hexham', '1015', 320.00, 650.00, 'POL_FS2025'),
       ('Abingdon, Didcot & Witney (South Oxfordshire)', '1131', 320.00, 650.00, 'POL_FS2025'),
       ('Aylesbury', '1132', 320.00, 650.00, 'POL_FS2025'),
       ('High Wycombe & Amersham', '1133', 320.00, 650.00, 'POL_FS2025'),
       ('Milton Keynes', '1134', 320.00, 650.00, 'POL_FS2025'),
       ('Bicester / North Oxon (Banbury)', '1135', 320.00, 650.00, 'POL_FS2025'),
       ('Oxford', '1136', 320.00, 650.00, 'POL_FS2025'),
       ('Reading', '1137', 320.00, 650.00, 'POL_FS2025'),
       ('Slough (East Berkshire)', '1138', 320.00, 650.00, 'POL_FS2025')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('West Berkshire (Newbury Etc)', '1139', 320.00, 650.00, 'POL_FS2025'),
       ('Aldershot / Petersfield (North East Hampshire)', '1140', 320.00, 650.00, 'POL_FS2025'),
       ('Andover / Basingstoke / Winchester (NW Hants)', '1141', 320.00, 650.00, 'POL_FS2025'),
       ('Isle Of Wight', '1142', 320.00, 650.00, 'POL_FS2025'),
       ('Portsmouth / Waterlooville (South East Hampshire)', '1143', 320.00, 650.00, 'POL_FS2025'),
       ('Gosport & Fareham', '1144', 320.00, 650.00, 'POL_FS2025'),
       ('Southampton (South West Hampshire)', '1145', 320.00, 650.00, 'POL_FS2025'),
       ('Grimsby & Cleethorpes', '1201', 320.00, 650.00, 'POL_FS2025'),
       ('Scunthorpe', '1202', 320.00, 650.00, 'POL_FS2025'),
       ('Hull', '1203', 320.00, 650.00, 'POL_FS2025')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Beverley / Bridlington', '1204', 320.00, 650.00, 'POL_FS2025'),
       ('Goole', '1205', 320.00, 650.00, 'POL_FS2025'),
       ('Northallerton & Richmond', '1206', 320.00, 650.00, 'POL_FS2025'),
       ('Harrogate & Ripon', '1207', 320.00, 650.00, 'POL_FS2025'),
       ('Skipton, Settle & Ingleton', '1208', 320.00, 650.00, 'POL_FS2025'),
       ('Scarborough / Whitby', '1209', 320.00, 650.00, 'POL_FS2025'),
       ('Malton & Ryedale', '1210', 320.00, 650.00, 'POL_FS2025'),
       ('York / Selby', '1211', 320.00, 650.00, 'POL_FS2025'),
       ('Barnsley', '1212', 320.00, 650.00, 'POL_FS2025'),
       ('Doncaster', '1213', 320.00, 650.00, 'POL_FS2025')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Rotherham', '1214', 320.00, 650.00, 'POL_FS2025'),
       ('Sheffield', '1215', 320.00, 650.00, 'POL_FS2025'),
       ('Halifax', '1216', 320.00, 650.00, 'POL_FS2025'),
       ('Huddersfield', '1217', 320.00, 650.00, 'POL_FS2025'),
       ('Dewsbury', '1218', 320.00, 650.00, 'POL_FS2025'),
       ('Bradford', '1219', 320.00, 650.00, 'POL_FS2025'),
       ('Keighley & Bingley', '1220', 320.00, 650.00, 'POL_FS2025'),
       ('Leeds', '1221', 320.00, 650.00, 'POL_FS2025'),
       ('Pontefract & Castleford', '1222', 320.00, 650.00, 'POL_FS2025'),
       ('Wakefield', '1223', 320.00, 650.00, 'POL_FS2025')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Barking', '1301', 320.00, 650.00, 'POL_FS2025'),
       ('Bexley', '1302', 320.00, 650.00, 'POL_FS2025'),
       ('Bishopsgate', '1303', 320.00, 650.00, 'POL_FS2025'),
       ('Brent', '1304', 320.00, 650.00, 'POL_FS2025'),
       ('Brentford', '1305', 320.00, 650.00, 'POL_FS2025'),
       ('Bromley', '1306', 320.00, 650.00, 'POL_FS2025'),
       ('Camberwell Green', '1307', 320.00, 650.00, 'POL_FS2025'),
       ('Central London', '1308', 320.00, 650.00, 'POL_FS2025'),
       ('Clerkenwell/Hampstead', '1309', 320.00, 650.00, 'POL_FS2025'),
       ('Croydon', '1310', 320.00, 650.00, 'POL_FS2025')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Ealing', '1311', 320.00, 650.00, 'POL_FS2025'),
       ('Enfield', '1312', 320.00, 650.00, 'POL_FS2025'),
       ('Greenwich/Woolwich', '1313', 320.00, 650.00, 'POL_FS2025'),
       ('Haringey', '1314', 320.00, 650.00, 'POL_FS2025'),
       ('Harrow', '1315', 320.00, 650.00, 'POL_FS2025'),
       ('Havering', '1316', 320.00, 650.00, 'POL_FS2025'),
       ('Heathrow', '1317', 320.00, 650.00, 'POL_FS2025'),
       ('Hendon/Barnet', '1318', 320.00, 650.00, 'POL_FS2025'),
       ('Highbury Corner', '1319', 320.00, 650.00, 'POL_FS2025'),
       ('Kingston-Upon-Thames', '1320', 320.00, 650.00, 'POL_FS2025')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Newham', '1321', 320.00, 650.00, 'POL_FS2025'),
       ('Old Street', '1322', 320.00, 650.00, 'POL_FS2025'),
       ('Redbridge', '1323', 320.00, 650.00, 'POL_FS2025'),
       ('Richmond-Upon-Thames', '1324', 320.00, 650.00, 'POL_FS2025'),
       ('South London', '1325', 320.00, 650.00, 'POL_FS2025'),
       ('Sutton', '1326', 320.00, 650.00, 'POL_FS2025'),
       ('Thames', '1327', 320.00, 650.00, 'POL_FS2025'),
       ('Tower Bridge', '1328', 320.00, 650.00, 'POL_FS2025'),
       ('Uxbridge', '1329', 320.00, 650.00, 'POL_FS2025'),
       ('Waltham Forest', '1330', 320.00, 650.00, 'POL_FS2025')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('West London', '1331', 320.00, 650.00, 'POL_FS2025'),
       ('Wimbledon', '1332', 320.00, 650.00, 'POL_FS2025'),
       ('Avon North & Thornbury', '2001', 320.00, 650.00, 'POL_FS2025'),
       ('Bath', '2002', 320.00, 650.00, 'POL_FS2025'),
       ('Mendip & South Somerset', '2003', 320.00, 650.00, 'POL_FS2025'),
       ('Bristol', '2004', 320.00, 650.00, 'POL_FS2025'),
       ('Sedgemore / Taunton Deane', '2005', 320.00, 650.00, 'POL_FS2025'),
       ('Weston-Super-Mare', '2006', 320.00, 650.00, 'POL_FS2025'),
       ('Central Dorset', '2007', 320.00, 650.00, 'POL_FS2025'),
       ('Bournemouth & Christchurch', '2008', 320.00, 650.00, 'POL_FS2025')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Poole East Dorset', '2009', 320.00, 650.00, 'POL_FS2025'),
       ('Bridport / West Dorset', '2010', 320.00, 650.00, 'POL_FS2025'),
       ('Salisbury', '2011', 320.00, 650.00, 'POL_FS2025'),
       ('Chippenham / Trowbridge', '2012', 320.00, 650.00, 'POL_FS2025'),
       ('Swindon', '2013', 320.00, 650.00, 'POL_FS2025'),
       ('Cheltenham', '2014', 320.00, 650.00, 'POL_FS2025'),
       ('Gloucester', '2015', 320.00, 650.00, 'POL_FS2025'),
       ('Stroud', '2016', 320.00, 650.00, 'POL_FS2025'),
       ('Barnstaple', '2017', 320.00, 650.00, 'POL_FS2025'),
       ('Exeter', '2018', 320.00, 650.00, 'POL_FS2025')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Plymouth', '2019', 320.00, 650.00, 'POL_FS2025'),
       ('East Cornwall', '2020', 320.00, 650.00, 'POL_FS2025'),
       ('Carrick / Kerrier (Camborne) / Penwith', '2021', 320.00, 650.00, 'POL_FS2025'),
       ('Teignbridge / Torbay', '2022', 320.00, 650.00, 'POL_FS2025'),
       ('Stoke on Trent / Leek', '3001', 320.00, 650.00, 'POL_FS2025'),
       ('Stafford / Cannock & Rugeley', '3002', 320.00, 650.00, 'POL_FS2025'),
       ('Lichfield & Tamworth / Burton Upon Trent / Uttoxeter', '3003', 320.00, 650.00, 'POL_FS2025'),
       ('Leamington / Nuneaton / Rugby', '3004', 320.00, 650.00, 'POL_FS2025'),
       ('Hereford / Leominster', '3005', 320.00, 650.00, 'POL_FS2025'),
       ('Kidderminster / Redditch', '3006', 320.00, 650.00, 'POL_FS2025')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Shrewsbury', '3007', 320.00, 650.00, 'POL_FS2025'),
       ('Telford', '3008', 320.00, 650.00, 'POL_FS2025'),
       ('Worcester', '3009', 320.00, 650.00, 'POL_FS2025'),
       ('Sandwell', '3010', 320.00, 650.00, 'POL_FS2025'),
       ('Wolverhampton & Seisdon', '3011', 320.00, 650.00, 'POL_FS2025'),
       ('Dudley & Halesowen', '3012', 320.00, 650.00, 'POL_FS2025'),
       ('Walsall', '3013', 320.00, 650.00, 'POL_FS2025'),
       ('Birmingham', '3014', 320.00, 650.00, 'POL_FS2025'),
       ('Solihull', '3015', 320.00, 650.00, 'POL_FS2025'),
       ('Coventry', '3016', 320.00, 650.00, 'POL_FS2025')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Amman Valley', '4001', 320.00, 650.00, 'POL_FS2025'),
       ('Carmarthen East Dyfed', '4002', 320.00, 650.00, 'POL_FS2025'),
       ('Llanelli', '4003', 320.00, 650.00, 'POL_FS2025'),
       ('Brecon & Radnor', '4004', 320.00, 650.00, 'POL_FS2025'),
       ('Mid Wales', '4005', 320.00, 650.00, 'POL_FS2025'),
       ('North Ceredigion / South Ceredigion', '4006', 320.00, 650.00, 'POL_FS2025'),
       ('Pembrokeshire', '4007', 320.00, 650.00, 'POL_FS2025'),
       ('East Gwent', '4008', 320.00, 650.00, 'POL_FS2025'),
       ('Newport', '4009', 320.00, 650.00, 'POL_FS2025'),
       ('Lower Rhymney Valley / North Bedwellty / South Bedwellty', '4010', 320.00, 650.00, 'POL_FS2025')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Bangor & Caernarfon', '4011', 320.00, 650.00, 'POL_FS2025'),
       ('Colwyn Bay', '4012', 320.00, 650.00, 'POL_FS2025'),
       ('Denbighshire', '4013', 320.00, 650.00, 'POL_FS2025'),
       ('Dolgellau', '4014', 320.00, 650.00, 'POL_FS2025'),
       ('Mold & Hawarden', '4015', 320.00, 650.00, 'POL_FS2025'),
       ('North Anglesey', '4016', 320.00, 650.00, 'POL_FS2025'),
       ('Pwllheli', '4017', 320.00, 650.00, 'POL_FS2025'),
       ('Wrexham', '4018', 320.00, 650.00, 'POL_FS2025'),
       ('Cardiff', '4019', 320.00, 650.00, 'POL_FS2025'),
       ('Vale of Glamorgan', '4020', 320.00, 650.00, 'POL_FS2025')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Cynon Valley', '4021', 320.00, 650.00, 'POL_FS2025'),
       ('Mid Glamorgan & Miskin', '4022', 320.00, 650.00, 'POL_FS2025'),
       ('Merthyr Tydfil', '4023', 320.00, 650.00, 'POL_FS2025'),
       ('Port Talbot', '4024', 320.00, 650.00, 'POL_FS2025'),
       ('Newcastle & Ogmore', '4025', 320.00, 650.00, 'POL_FS2025'),
       ('Neath', '4026', 320.00, 650.00, 'POL_FS2025'),
       ('Swansea', '4027', 320.00, 650.00, 'POL_FS2025'),
       ('Bootle & Crosby', '5001', 320.00, 650.00, 'POL_FS2025'),
       ('Southport', '5002', 320.00, 650.00, 'POL_FS2025'),
       ('Liverpool', '5003', 320.00, 650.00, 'POL_FS2025')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('St Helens', '5004', 320.00, 650.00, 'POL_FS2025'),
       ('Knowsley', '5005', 320.00, 650.00, 'POL_FS2025'),
       ('Wirral', '5006', 320.00, 650.00, 'POL_FS2025'),
       ('Crewe & Nantwich / Sandbach & Congleton / Macclesfield', '6001', 320.00, 650.00, 'POL_FS2025'),
       ('Warrington / Halton', '6002', 320.00, 650.00, 'POL_FS2025'),
       ('Chester / Vale Royal (Northwich)', '6003', 320.00, 650.00, 'POL_FS2025'),
       ('Barrow In Furness', '6004', 320.00, 650.00, 'POL_FS2025'),
       ('Kendal & Windermere', '6005', 320.00, 650.00, 'POL_FS2025'),
       ('Penrith / Carlisle', '6006', 320.00, 650.00, 'POL_FS2025'),
       ('Whitehaven / Workington', '6007', 320.00, 650.00, 'POL_FS2025')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Manchester', '6008', 320.00, 650.00, 'POL_FS2025'),
       ('Stockport', '6009', 320.00, 650.00, 'POL_FS2025'),
       ('Trafford', '6010', 320.00, 650.00, 'POL_FS2025'),
       ('Salford', '6011', 320.00, 650.00, 'POL_FS2025'),
       ('Bolton', '6012', 320.00, 650.00, 'POL_FS2025'),
       ('Bury', '6013', 320.00, 650.00, 'POL_FS2025'),
       ('Wigan', '6014', 320.00, 650.00, 'POL_FS2025'),
       ('Rochdale / Middleton', '6015', 320.00, 650.00, 'POL_FS2025'),
       ('Tameside', '6016', 320.00, 650.00, 'POL_FS2025'),
       ('Oldham', '6017', 320.00, 650.00, 'POL_FS2025')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Burnley / Rossendale', '6018', 320.00, 650.00, 'POL_FS2025'),
       ('Blackburn / Accrington / Ribble Valley', '6019', 320.00, 650.00, 'POL_FS2025'),
       ('Blackpool', '6020', 320.00, 650.00, 'POL_FS2025'),
       ('Fleetwood', '6021', 320.00, 650.00, 'POL_FS2025'),
       ('Lancaster', '6022', 320.00, 650.00, 'POL_FS2025'),
       ('Chorley / Ormskirk / South Ribble & Leyland', '6023', 320.00, 650.00, 'POL_FS2025'),
       ('Preston', '6024', 320.00, 650.00, 'POL_FS2025'),
       ('Dartford & Gravesend', '7001', 320.00, 650.00, 'POL_FS2025'),
       ('Ashford & Tenterden / Dover / Folkestone', '7002', 320.00, 650.00, 'POL_FS2025'),
       ('Medway', '7003', 320.00, 650.00, 'POL_FS2025')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Swale', '7004', 320.00, 650.00, 'POL_FS2025'),
       ('Maidstone & West Malling', '7005', 320.00, 650.00, 'POL_FS2025'),
       ('Canterbury / Thanet', '7006', 320.00, 650.00, 'POL_FS2025'),
       ('West Kent (Tonbridge)', '7007', 320.00, 650.00, 'POL_FS2025'),
       ('Guildford & Farnham', '7008', 320.00, 650.00, 'POL_FS2025'),
       ('North West Surrey (Woking)', '7009', 320.00, 650.00, 'POL_FS2025'),
       ('South East Surrey', '7010', 320.00, 650.00, 'POL_FS2025'),
       ('Epsom', '7011', 320.00, 650.00, 'POL_FS2025'),
       ('Staines', '7012', 320.00, 650.00, 'POL_FS2025'),
       ('Brighton & Hove & Lewes', '7013', 320.00, 650.00, 'POL_FS2025'),
       ('Chichester & District', '7014', 320.00, 650.00, 'POL_FS2025'),
       ('Crawley / Horsham', '7015', 320.00, 650.00, 'POL_FS2025')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Hastings', '7016', 320.00, 650.00, 'POL_FS2025'),
       ('Worthing', '7017', 320.00, 650.00, 'POL_FS2025'),
       ('Eastbourne', '7018', 320.00, 650.00, 'POL_FS2025'),
       ('East Derbyshire (Ripley) / Ilkeston', '8001', 320.00, 650.00, 'POL_FS2025'),
       ('Ashbourne / Matlock / High Peak (Buxton)', '8002', 320.00, 650.00, 'POL_FS2025'),
       ('Chesterfield', '8003', 320.00, 650.00, 'POL_FS2025'),
       ('Derby / Swadlincote', '8004', 320.00, 650.00, 'POL_FS2025'),
       ('Ashby & Coalville / Loughborough / Melton Mowbray', '8005', 320.00, 650.00, 'POL_FS2025'),
       ('Leicester', '8006', 320.00, 650.00, 'POL_FS2025'),
       ('Hinckley / Market Harborough', '8007', 320.00, 650.00, 'POL_FS2025')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Boston / Bourne / Stamford', '8008', 320.00, 650.00, 'POL_FS2025'),
       ('Skegness', '8009', 320.00, 650.00, 'POL_FS2025'),
       ('Lincoln / Gainsborough', '8010', 320.00, 650.00, 'POL_FS2025'),
       ('Grantham & Sleaford', '8011', 320.00, 650.00, 'POL_FS2025'),
       ('Mansfield', '8012', 320.00, 650.00, 'POL_FS2025'),
       ('Newark', '8013', 320.00, 650.00, 'POL_FS2025'),
       ('Nottingham', '8014', 320.00, 650.00, 'POL_FS2025'),
       ('Worksop & East Retford', '8015', 320.00, 650.00, 'POL_FS2025'),
       ('Corby (Kettering) / Wellingborough', '8016', 320.00, 650.00, 'POL_FS2025'),
       ('Northampton', '8017', 320.00, 650.00, 'POL_FS2025')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Bedford', '9001', 320.00, 650.00, 'POL_FS2025'),
       ('Luton', '9002', 320.00, 650.00, 'POL_FS2025'),
       ('Cambridge', '9003', 320.00, 650.00, 'POL_FS2025'),
       ('Ely', '9004', 320.00, 650.00, 'POL_FS2025'),
       ('Huntingdon', '9005', 320.00, 650.00, 'POL_FS2025'),
       ('March & Wisbech', '9006', 320.00, 650.00, 'POL_FS2025'),
       ('Peterborough', '9007', 320.00, 650.00, 'POL_FS2025'),
       ('Basildon', '9008', 320.00, 650.00, 'POL_FS2025'),
       ('Brentwood', '9009', 320.00, 650.00, 'POL_FS2025'),
       ('Braintree', '9010', 320.00, 650.00, 'POL_FS2025')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Clacton & Harwich / Colchester', '9011', 320.00, 650.00, 'POL_FS2025'),
       ('Grays', '9012', 320.00, 650.00, 'POL_FS2025'),
       ('Harlow & Loughton', '9013', 320.00, 650.00, 'POL_FS2025'),
       ('Stansted', '9014', 320.00, 650.00, 'POL_FS2025'),
       ('Rayleigh / Southend On Sea', '9015', 320.00, 650.00, 'POL_FS2025'),
       ('Chelmsford / Witham', '9016', 320.00, 650.00, 'POL_FS2025'),
       ('Dacorum (Hemel Hempstead)', '9017', 320.00, 650.00, 'POL_FS2025'),
       ('Bishop''s Stortford / East Hertfordshire', '9018', 320.00, 650.00, 'POL_FS2025'),
       ('Stevenage & North Hertfordshire', '9019', 320.00, 650.00, 'POL_FS2025'),
       ('St Albans', '9020', 320.00, 650.00, 'POL_FS2025')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

INSERT INTO police_station_fees (ps_scheme_name, ps_scheme_id, fixed_fee, escape_threshold, fee_scheme_code)
VALUES ('Watford', '9021', 320.00, 650.00, 'POL_FS2025'),
       ('Cromer & North Walsham', '9022', 320.00, 650.00, 'POL_FS2025'),
       ('Great Yarmouth', '9023', 320.00, 650.00, 'POL_FS2025'),
       ('Kings Lynn & West Norfolk', '9024', 320.00, 650.00, 'POL_FS2025'),
       ('Norwich & District', '9025', 320.00, 650.00, 'POL_FS2025'),
       ('Diss / Thetford', '9026', 320.00, 650.00, 'POL_FS2025'),
       ('Dereham', '9027', 320.00, 650.00, 'POL_FS2025'),
       ('Lowestoft / Beccles & Halesworth / Aldeburgh', '9028', 320.00, 650.00, 'POL_FS2025'),
       ('Felixstowe / Ipswich & District / Woodbridge', '9029', 320.00, 650.00, 'POL_FS2025'),
       ('Sudbury & Hadleigh / Bury St Edmunds / Haverhill / Newmarket', '9030', 320.00, 650.00, 'POL_FS2025')
ON CONFLICT (ps_scheme_id, fee_scheme_code) DO NOTHING;

-- Fee Codes for Prison Law

INSERT INTO fee (fee_code,description,fee_scheme_code,fixed_fee,escape_threshold_limit,category_type,total_limit,fee_type) VALUES
  ('PRIA','Free Standing Advice and Assistance','PRISON_FS2016',200.75,602.25,'PRISON_LAW',NULL,'FIXED'),
  ('PRIB1','Disciplinary Cases – Advocacy Assistance - lower standard fee','PRISON_FS2016',203.93,NULL,'PRISON_LAW_LOWER_STANDARD',357.06,'FIXED'),
  ('PRIC1','Parole Board Cases – Advocacy Assistance - lower standard fee','PRISON_FS2016',437.21,NULL,'PRISON_LAW_LOWER_STANDARD',933.93,'FIXED'),
  ('PRID1','Advocacy Assistance at Sentence Reviews - lower standard fee','PRISON_FS2016',437.21,NULL,'PRISON_LAW_LOWER_STANDARD',357.06,'FIXED'),
  ('PRIE1','Advocacy Assistance at Parole Board Reconsideration Hearings - lower standard fee','PRISON_FS2016',203.93,NULL,'PRISON_LAW_LOWER_STANDARD',933.93,'FIXED'),
  ('PRIB2','Disciplinary Cases – Advocacy Assistance - higher standard fee','PRISON_FS2016',564.16,1691.69,'PRISON_LAW_HIGHER_STANDARD',NULL,'FIXED'),
  ('PRIC2','Parole Board Cases – Advocacy Assistance - higher standard fee','PRISON_FS2016',1454.44,4362.54,'PRISON_LAW_HIGHER_STANDARD',NULL,'FIXED'),
  ('PRID2','Advocacy Assistance at Sentence Reviews - higher standard fee','PRISON_FS2016',564.16,1691.69,'PRISON_LAW_HIGHER_STANDARD',NULL,'FIXED'),
  ('PRIE2','Advocacy Assistance at Parole Board Reconsideration Hearings - higher standard fee','PRISON_FS2016',1454.44,4362.54,'PRISON_LAW_HIGHER_STANDARD',NULL,'FIXED')
ON CONFLICT (fee_code, fee_scheme_code) DO NOTHING;

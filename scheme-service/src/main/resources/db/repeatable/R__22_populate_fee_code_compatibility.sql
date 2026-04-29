-- Prison Fee Code Compatibility
INSERT INTO fee_code_groupings (code, description) VALUES
   ('PRISON_FIXED', 'Prison fixed fee (PRIA)'),
   ('PRISON_LOW', 'Codes ending in 1'),
   ('PRISON_HIGH', 'Codes ending in 2');

-- Fixed
UPDATE fee_code_information
SET grouping_code = 'PRISON_FIXED'
WHERE fee_code = 'PRIA';

-- Lower tier (ends in 1)
UPDATE fee_code_information
SET grouping_code = 'PRISON_LOW'
WHERE fee_code IN ('PRIB1', 'PRIC1', 'PRID1', 'PRIE1');

-- Higher tier (ends in 2)
UPDATE fee_code_information
SET grouping_code = 'PRISON_HIGH'
WHERE fee_code IN ('PRIB2', 'PRIC2', 'PRID2', 'PRIE2');

INSERT INTO fee_compatibility_mapping (
    source_grouping_code, target_grouping_code, compatible
)
VALUES
    ('PRISON_FIXED', 'PRISON_LOW', false),
    ('PRISON_FIXED', 'PRISON_HIGH', false),
    ('PRISON_LOW', 'PRISON_FIXED', false),
    ('PRISON_HIGH', 'PRISON_FIXED', false),
    ('PRISON_LOW', 'PRISON_HIGH', false),
    ('PRISON_HIGH', 'PRISON_LOW', false),
    ('PRISON_LOW', 'PRISON_LOW', true),
    ('PRISON_HIGH', 'PRISON_HIGH', true);

-- Police Station Fee Code Compatibility
INSERT INTO fee_code_groupings (code, description) VALUES
   ('POLICE_FIXED_INVC', 'Police fixed fee INVC'),
   ('POLICE_FIXED_INVB', 'Police fixed fees INVB1 & INVB2'),
   ('POLICE_STANDARD', 'Standard police station fees');

-- INVC
UPDATE fee_code_information
SET grouping_code = 'POLICE_FIXED_INVC'
WHERE fee_code = 'INVC';

-- INVB1 & INVB2
UPDATE fee_code_information
SET grouping_code = 'POLICE_FIXED_INVB'
WHERE fee_code IN ('INVB1', 'INVB2');

-- Standard group
UPDATE fee_code_information
SET grouping_code = 'POLICE_STANDARD'
WHERE fee_code IN ('INVA', 'INVE', 'INVH', 'INVK', 'INVL', 'INVM');

INSERT INTO fee_compatibility_mapping (
    source_grouping_code, target_grouping_code, compatible
)
VALUES
    ('POLICE_STANDARD', 'POLICE_FIXED_INVC', false),
    ('POLICE_FIXED_INVB', 'POLICE_STANDARD', false),
    ('POLICE_STANDARD', 'POLICE_FIXED_INVB', false),
    ('POLICE_FIXED_INVC', 'POLICE_FIXED_INVB', false),
    ('POLICE_FIXED_INVB', 'POLICE_FIXED_INVC', false),
    ('POLICE_STANDARD', 'POLICE_STANDARD', true),
    ('POLICE_FIXED_INVB', 'POLICE_FIXED_INVB', true),
    ('POLICE_FIXED_INVC', 'POLICE_FIXED_INVC', true);

-- Other Criminal Proceedings Fee Code Compatibility
INSERT INTO fee_code_groupings (code, description) VALUES
   ('OTHER_CRIME_STANDARD', 'PROP + PROH codes'),
   ('PROD', 'PROD special handling'),
   ('PROW', 'PROW special handling');

-- Standard group
UPDATE fee_code_information
SET grouping_code = 'OTHER_CRIME_STANDARD'
WHERE fee_code IN ('PROP1', 'PROP2', 'PROH', 'PROH1', 'PROH2');

-- PROD
UPDATE fee_code_information
SET grouping_code = 'PROD'
WHERE fee_code = 'PROD';

-- PROW
UPDATE fee_code_information
SET grouping_code = 'PROW'
WHERE fee_code = 'PROW';

INSERT INTO fee_compatibility_mapping (
    source_grouping_code, target_grouping_code, compatible
)
VALUES
    ('PROD', 'OTHER_CRIME_STANDARD', false),
    ('PROD', 'PROW', false),
    ('OTHER_CRIME_STANDARD', 'PROD', false),
    ('PROW', 'OTHER_CRIME_STANDARD', false),
    ('PROW', 'PROD', false),
    ('OTHER_CRIME_STANDARD', 'PROW', false),
    ('OTHER_CRIME_STANDARD', 'OTHER_CRIME_STANDARD', true);

-- Youth Court Work Fee Code Compatibility
INSERT INTO fee_code_groupings (code, description) VALUES
    ('YOUE', 'Youth Court Work Fee'),
    ('YOUL', 'Youth Court Work Fee'),
   ('YOUF', 'Youth Court Work Fixed Fee'),
   ('YOUK', 'Youth Court Work Low Tier'),
   ('YOUX', 'Youth Court Work High Tier'),
   ('YOUY', 'Youth Court Work Other');

-- YOUE
UPDATE fee_code_information
SET grouping_code = 'YOUE'
WHERE fee_code IN ('YOUE1', 'YOUE2', 'YOUE3', 'YOUE4');

-- YOUL
UPDATE fee_code_information
SET grouping_code = 'YOUL'
WHERE fee_code IN ('YOUL1', 'YOUL2', 'YOUL3', 'YOUL4');

-- YOUF
UPDATE fee_code_information
SET grouping_code = 'YOUF'
WHERE fee_code IN ('YOUF1', 'YOUF2', 'YOUF3', 'YOUF4');

-- YOUK
UPDATE fee_code_information
SET grouping_code = 'YOUK'
WHERE fee_code IN ('YOUK1', 'YOUK2', 'YOUK3', 'YOUK4');

-- YOUX
UPDATE fee_code_information
SET grouping_code = 'YOUX'
WHERE fee_code IN ('YOUX1', 'YOUX2', 'YOUX3', 'YOUX4');

-- YOUY
UPDATE fee_code_information
SET grouping_code = 'YOUY'
WHERE fee_code IN ('YOUY1', 'YOUY2', 'YOUY3', 'YOUY4');

-- Insert one-way fee code to fee code compatibility mappings for Youth Court Work (Including YOUY)
INSERT INTO fee_compatibility_mapping (source_grouping_code, target_grouping_code, compatible)
VALUES
    ('YOUF', 'YOUF', true),
    ('YOUF', 'YOUX', true),
    ('YOUF', 'YOUE', true),
    ('YOUK', 'YOUK', true),
    ('YOUK', 'YOUL', true),
    ('YOUK', 'YOUY', true),
    ('YOUX', 'YOUX', true),
    ('YOUX', 'YOUF', true),
    ('YOUX', 'YOUE', true),
    ('YOUE', 'YOUE', true),
    ('YOUE', 'YOUF', true),
    ('YOUE', 'YOUX', true),
    ('YOUL', 'YOUL', true),
    ('YOUL', 'YOUY', true),
    ('YOUL', 'YOUK', true),
    ('YOUY', 'YOUY', true),
    ('YOUY', 'YOUK', true),
    ('YOUY', 'YOUL', true),
    ('YOUY', 'YOUF', false),
    ('YOUY', 'YOUE', false),
    ('YOUY', 'YOUX', false),
    ('YOUF', 'YOUK', false),
    ('YOUF', 'YOUY', false),
    ('YOUK', 'YOUF', false),
    ('YOUX', 'YOUK', false),
    ('YOUX', 'YOUY', false),
    ('YOUE', 'YOUK', false),
    ('YOUE', 'YOUY', false),
    ('YOUL', 'YOUF', false),
    ('YOUL', 'YOUX', false),
    ('YOUL', 'YOUE', false);

-- Magistrates Court Work Fee Code Compatibility
INSERT INTO fee_code_groupings (code, description) VALUES
   ('PROE', 'Magistrates Court Work E Codes'),
   ('PROF', 'Magistrates Court Work F Codes'),
   ('PROJ_UNDESIGNATED', 'Undesignated Magistrates Court Work J Codes'),
   ('PROJ_DESIGNATED', 'Designated Magistrates Court Work J Codes'),
   ('PROK', 'Magistrates Court Work K Codes'),
   ('PROL', 'Magistrates Court Work L Codes'),
   ('PROV', 'Magistrates Court Work V Codes'),
   ('PROT', 'Magistrates Court Work T Codes'),
   ('PROU', 'Magistrates Court Work U Codes');

-- PROE
UPDATE fee_code_information
SET grouping_code = 'PROE'
WHERE fee_code IN ('PROE1', 'PROE2', 'PROE3');

-- PROF
UPDATE fee_code_information
SET grouping_code = 'PROF'
WHERE fee_code IN ('PROF1', 'PROF2', 'PROF3');

-- PROJ Undesignated
UPDATE fee_code_information
SET grouping_code = 'PROJ_UNDESIGNATED'
WHERE fee_code IN ('PROJ1', 'PROJ2', 'PROJ3', 'PROJ4');

-- PROJ Designated
UPDATE fee_code_information
SET grouping_code = 'PROJ_DESIGNATED'
WHERE fee_code IN ('PROJ5', 'PROJ6', 'PROJ7', 'PROJ8');

-- PROK
UPDATE fee_code_information
SET grouping_code = 'PROK'
WHERE fee_code IN ('PROK1', 'PROK2', 'PROK3');

-- PROL
UPDATE fee_code_information
SET grouping_code = 'PROL'
WHERE fee_code IN ('PROL1', 'PROL2', 'PROL3');

-- PROV
UPDATE fee_code_information
SET grouping_code = 'PROV'
WHERE fee_code IN ('PROV1', 'PROV2', 'PROV3', 'PROV4');

-- PROT
UPDATE fee_code_information
SET grouping_code = 'PROT'
WHERE fee_code IN ('PROT');

-- PROU
UPDATE fee_code_information
SET grouping_code = 'PROU'
WHERE fee_code IN ('PROU');

INSERT INTO fee_compatibility_mapping (source_grouping_code, target_grouping_code, compatible)
VALUES
    ('PROE', 'PROE', true),
    ('PROE', 'PROF', true),
    ('PROE', 'PROJ_UNDESIGNATED', true),
    ('PROE', 'PROV', true),
    ('PROF', 'PROF', true),
    ('PROF', 'PROE', true),
    ('PROF', 'PROJ_UNDESIGNATED', true),
    ('PROF', 'PROV', true),
    ('PROJ_UNDESIGNATED', 'PROJ_UNDESIGNATED', true),
    ('PROJ_UNDESIGNATED', 'PROE', true),
    ('PROJ_UNDESIGNATED', 'PROF', true),
    ('PROJ_UNDESIGNATED', 'PROV', true),
    ('PROJ_DESIGNATED', 'PROJ_DESIGNATED', true),
    ('PROJ_DESIGNATED', 'PROK', true),
    ('PROJ_DESIGNATED', 'PROL', true),
    ('PROK', 'PROK', true),
    ('PROK', 'PROJ_DESIGNATED', true),
    ('PROK', 'PROL', true),
    ('PROL', 'PROL', true),
    ('PROL', 'PROJ_DESIGNATED', true),
    ('PROL', 'PROK', true),
    ('PROV', 'PROV', true),
    ('PROV', 'PROE', true),
    ('PROV', 'PROF', true),
    ('PROT', 'PROT', true),
    ('PROU', 'PROU', true),
    ('PROT', 'PROU', true),
    ('PROU', 'PROT', true),
    ('PROV', 'PROJ_UNDESIGNATED', true),
    ('PROE', 'PROJ_DESIGNATED', false),
    ('PROE', 'PROK', false),
    ('PROE', 'PROL', false),
    ('PROE', 'PROT', false),
    ('PROE', 'PROU', false),
    ('PROF', 'PROJ_DESIGNATED', false),
    ('PROF', 'PROK', false),
    ('PROF', 'PROL', false),
    ('PROF', 'PROT', false),
    ('PROF', 'PROU', false),
    ('PROJ_UNDESIGNATED', 'PROJ_DESIGNATED', false),
    ('PROJ_UNDESIGNATED', 'PROK', false),
    ('PROJ_UNDESIGNATED', 'PROL', false),
    ('PROJ_UNDESIGNATED', 'PROT', false),
    ('PROJ_UNDESIGNATED', 'PROU', false),
    ('PROJ_DESIGNATED', 'PROJ_UNDESIGNATED', false),
    ('PROJ_DESIGNATED', 'PROE', false),
    ('PROJ_DESIGNATED', 'PROF', false),
    ('PROJ_DESIGNATED', 'PROV', false),
    ('PROJ_DESIGNATED', 'PROT', false),
    ('PROJ_DESIGNATED', 'PROU', false),
    ('PROK', 'PROJ_UNDESIGNATED', false),
    ('PROK', 'PROE', false),
    ('PROK', 'PROF', false),
    ('PROK', 'PROV', false),
    ('PROK', 'PROT', false),
    ('PROK', 'PROU', false),
    ('PROV', 'PROJ_DESIGNATED', false),
    ('PROV', 'PROK', false),
    ('PROV', 'PROL', false),
    ('PROV', 'PROT', false),
    ('PROV', 'PROU', false);

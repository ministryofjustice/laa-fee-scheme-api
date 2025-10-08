INSERT INTO area_of_law_type
(area_of_law_type_id, code, description)
VALUES (1,'LEGAL_HELP', 'Legal Help'),
       (2,'CRIME_LOWER', 'Crime Lower'),
       (3,'MEDIATION', 'Mediation')
ON CONFLICT (code) DO NOTHING;

INSERT INTO category_of_law_type (category_of_law_type_id, code, description, area_of_law_type_id)
VALUES (1,'CRIME', 'Crime', 2),
       (2,'ALL', 'All Classes', 2),
       (3,'INVEST', 'Criminal Investigations and Criminal Proceedings', 2),
       (4,'PRISON', 'Prison Law', 2),
       (5,'APPEALS', 'Appeals and Reviews', 2),
       (6,'MEDI', 'Mediation', 3),
       (7,'MAT', 'Family', 1),
       (8,'DISC', 'Discrimination', 1),
       (9,'PUB', 'Public Law', 1),
       (10,'MED', 'Clinical Negligence', 1),
       (11,'IMMAS', 'Immigration - Asylum', 1),
       (12,'CON', 'Consumer General Contract', 1),
       (13,'HOU', 'Housing', 1),
       (14,'ELA', 'Early Legal Advice', 1),
       (15,'AAP', 'Claims Against Public Authorities', 1),
       (16,'WB', 'Welfare Benefits', 1),
       (17,'EDU', 'Education', 1),
       (18,'COM', 'Community Care', 1),
       (19,'DEB', 'Debt', 1),
       (20,'IMMOT', 'Immigration', 1),
       (21,'MHE', 'Mental Health', 1),
       (22,'EMP', 'Employment', 1),
       (23,'MSC', 'Residual (Miscellaneous)', 1),
       (24,'PI', 'Personal Injury', 1),
       (25,'RESIDUAL', 'Residual List', 1)
ON CONFLICT (code) DO NOTHING;


INSERT INTO fee_scheme_category_type (fee_scheme_category_type_id, fee_scheme_category_name)
VALUES (1,'Immigration & Asylum'),
       (2,'Mental Health'),
       (3,'Family'),
       (4,'Mediation'),
       (5,'Community Care '),
       (6,'Claims Against Public Authorities'),
       (7,'Clinical Negligence'),
       (8,'Debt'),
       (9,'Discrimination'),
       (10,'Education'),
       (11,'Housing - HLPAS'),
       (12,'Housing'),
       (13,'Miscellaneous'),
       (14,'Public Law'),
       (15,'Welfare Benefits '),
       (16,'Police Station'),
       (17,'Police Other'),
       (18,'Magistrates & Youth Court'),
       (19,'Prison Law'),
       (20,'Advocacy Assistance'),
       (21,'Associated Civil')
ON CONFLICT (fee_scheme_category_name) DO NOTHING;

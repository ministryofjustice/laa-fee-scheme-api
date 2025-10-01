INSERT INTO category_of_law_look_up (category_code, full_description, area_of_law, fee_code)
    VALUES ('AAP', 'Claims Against Public Authorities', 'Legal Help', 'CAPA')
ON CONFLICT (category_code, fee_code) DO NOTHING;


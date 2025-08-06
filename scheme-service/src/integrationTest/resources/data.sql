-- Used to seed the test in-memory database
INSERT INTO fee_schemes(scheme_code, scheme_name, valid_from, valid_to)
VALUES ('SCHEME1', 'Scheme One', '2023-12-01', NULL);

INSERT INTO fee(fee_id, fee_code, fee_scheme_code, description, total_fee, profit_cost_limit, disbursement_limit,
                escape_threshold_limit, prior_authority_applicable, schedule_reference, ho_interview_bolt_on,
                oral_cmrh_bolt_on, telephone_cmrh_bolt_on, substantive_hearing_bolt_on, adjorn_hearing_bolt_on, region)
VALUES (1, 'FEE1', 'SCHEME1', 'Fee-Description',1000.10, 2000.00, 3000.00, 4000.00, false, true, 100.50, 95.60, 45.30, 150.00, 75.00,
        'Region One');

INSERT INTO category_of_law_look_up(category_of_law_look_up_id, category_code, full_description, area_of_law, fee_code)
VALUES (456, 'CAT1', 'Category One Description', 'Area of Law One', 'FEE1');

INSERT INTO police_station_fees(police_station_fees_id, criminal_justice_area, police_station_name, police_station_code,
                                fixed_fee, escape_threshold, fee_scheme_code)
VALUES (123, 'Crime Area One', 'Police Station One', 'PS1', 500.00, 600.00, 'SCHEME1');


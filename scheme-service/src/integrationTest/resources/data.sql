-- Used to seed the test in-memory database
INSERT INTO fee_schemes(scheme_code, scheme_name, valid_from, valid_to) VALUES('SCHEME1', 'Scheme One', '2023-12-01', NULL);

INSERT INTO fee(fee_id, fee_code, fee_scheme_code, total_fee, profit_cost_limit, disbursement_limit, escape_threshold_limit, prior_authority_applicable, schedule_reference) VALUES(1, 'FEE1', 'SCHEME1', 1000.10, 2000.00, 3000.00, 4000.00, false, true);

INSERT INTO vat_rates(vat_rate_id, rate_percentage, valid_from, valid_to) VALUES(1, 20.00, '2023-12-01', NULL);

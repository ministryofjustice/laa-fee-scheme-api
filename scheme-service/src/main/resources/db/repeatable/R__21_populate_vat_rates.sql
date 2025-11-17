INSERT INTO vat_rates (start_date, vat_rate)
VALUES ('1991-03-19', 17.5),
       ('2008-12-01', 15),
       ('2010-01-01', 17.5),
       ('2011-01-04', 20)
ON CONFLICT (start_date, vat_rate) DO NOTHING;
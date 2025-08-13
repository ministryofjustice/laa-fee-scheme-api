ALTER TABLE category_of_law_look_up
    ADD CONSTRAINT uq_category_of_law_look_up_category_code_fee_code UNIQUE (category_code, fee_code);

ALTER TABLE police_station_fees
    ADD CONSTRAINT uq_police_station_fees_category_code_fee_code UNIQUE (police_station_code, fee_scheme_code);

ALTER TABLE fee
    ADD CONSTRAINT uq_fee_scheme_code_fee_code UNIQUE (fee_code, fee_scheme_code);

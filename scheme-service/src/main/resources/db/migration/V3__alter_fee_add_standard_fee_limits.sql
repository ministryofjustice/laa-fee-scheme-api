ALTER TABLE fee
    ADD COLUMN IF NOT EXISTS lower_standard_fee_limit NUMERIC(10, 2) NULL,
    ADD COLUMN IF NOT EXISTS higher_standard_fee_limit NUMERIC(10, 2) NULL;

ALTER TABLE fee
    RENAME COLUMN mediation_session_one TO mediation_fee_lower;

ALTER TABLE fee
    RENAME COLUMN mediation_session_two TO mediation_fee_higher;